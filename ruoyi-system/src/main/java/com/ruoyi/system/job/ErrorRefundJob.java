package com.ruoyi.system.job;

import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.mapper.OrderPayTypeMapper;
import com.ruoyi.system.pay.application.dto.request.OrderRefundRequest;
import com.ruoyi.system.pay.application.service.PayOrderApplicationService;
import com.ruoyi.system.pay.application.vo.RefundResultVo;
import com.ruoyi.system.pay.domain.valueobject.RefundMode;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 差错退款定时任务
 * 查询状态为 COMPLETED 且存在多个支付渠道的订单，执行差错退款
 *
 * @author 陈湘岳
 * @date 2026/3/23
 */
@Slf4j
@Component
public class ErrorRefundJob {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderPayTypeMapper orderPayTypeMapper;

    @Resource
    private PayOrderApplicationService payOrderApplicationService;

    @Resource
    private ThreadPoolTaskExecutor errorRefundExecutor;

    /**
     * 每批处理的订单数量
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 单个订单处理超时时间（秒）
     */
    private static final int ORDER_TIMEOUT_SECONDS = 30;

    @Autowired
    private TransactionTemplate transactionTemplate;

    /**
     * 差错退款定时任务
     * 在XXL-JOB调度中心配置：
     * - JobHandler: errorRefundJob
     * - 执行策略: 根据业务需求配置（如每10分钟执行一次）
     * - 任务参数: 可选，传入limit值（默认100）
     */
    @XxlJob("errorRefundJob")
    public void execute() {
        String jobParam = XxlJobHelper.getJobParam();
        int limit = BATCH_SIZE;
        if (jobParam != null && !jobParam.trim().isEmpty()) {
            try {
                limit = Integer.parseInt(jobParam.trim());
            } catch (NumberFormatException e) {
                LogEsUtil.warn("差错退款任务参数解析失败，使用默认值: " + BATCH_SIZE);
            }
        }

        LogEsUtil.info("开始执行差错退款定时任务，查询数量限制: " + limit);
        XxlJobHelper.log("开始执行差错退款定时任务，查询数量限制: " + limit);

        try {
            // 1. 查询需要差错退款的订单（COMPLETED状态且微信+支付宝虚拟订单号都存在）
            List<Order> orders = orderMapper.findCompletedOrdersForErrorRefund(limit);

            if (orders == null || orders.isEmpty()) {
                LogEsUtil.info("没有需要差错退款的订单");
                XxlJobHelper.log("没有需要差错退款的订单");
                XxlJobHelper.handleSuccess("没有需要处理的订单");
                return;
            }

            LogEsUtil.info("查询到 " + orders.size() + " 个需要差错退款的订单");
            XxlJobHelper.log("查询到 " + orders.size() + " 个需要差错退款的订单");

            // 2. 使用多线程并行处理
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(orders.size());

            for (Order order : orders) {
                CompletableFuture.runAsync(() -> {
                    try {
                        // 每个订单在独立事务中处理
                        transactionTemplate.execute(status->{
                            processErrorRefund(order);
                            return null;
                        });
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        //重试次数+1
                        orderPayTypeMapper.addRetryCount(order.getId());
                        LogEsUtil.error("订单差错退款失败，订单id: " + order.getId(), e);
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                }, errorRefundExecutor);
            }

            // 3. 等待所有任务完成（带超时）
            boolean completed = latch.await(ORDER_TIMEOUT_SECONDS * orders.size(), TimeUnit.SECONDS);
            if (!completed) {
                LogEsUtil.warn("差错退款任务处理超时，部分订单可能未处理完成");
                XxlJobHelper.log("差错退款任务处理超时，部分订单可能未处理完成");
            }

            String resultMsg = String.format("差错退款任务执行完成，成功: %d, 失败: %d, 总计: %d",
                    successCount.get(), failCount.get(), orders.size());
            LogEsUtil.info(resultMsg);
            XxlJobHelper.log(resultMsg);
            XxlJobHelper.handleSuccess(resultMsg);

        } catch (Exception e) {
            LogEsUtil.error("差错退款定时任务执行失败", e);
            XxlJobHelper.log("差错退款定时任务执行失败: " + e.getMessage());
            XxlJobHelper.handleFail("差错退款任务执行失败: " + e.getMessage());
        }
    }

    /**
     * 处理单个订单的差错退款
     * 使用独立事务，确保每个订单的退款操作原子性
     *
     * @param order 订单对象
     */
    public void processErrorRefund(Order order) {
        String orderId = order.getId();
        LogEsUtil.info("开始处理订单差错退款，订单id: " + orderId);

        try {
            // 构建差错退款请求
            OrderRefundRequest request = new OrderRefundRequest();
            request.setOrderId(orderId);
            // 差错退款模式
            request.setRefundMode(RefundMode.ERROR_REFUND.getCode());
            request.setRefundToBalance(false);
            request.setRefundReason("定时任务：差错退款");

            // 调用退款服务
            RefundResultVo result = payOrderApplicationService.refundByOrder(request);

            if (result.getSuccess()) {
                orderPayTypeMapper.updateIsCheckInt(orderId);
                LogEsUtil.info("订单差错退款成功，订单id: " + orderId +
                        ", 退款金额: " + result.getTotalRefundAmount());
            } else {
                LogEsUtil.warn("订单差错退款失败，订单id: " + orderId +
                        ", 原因: " + result.getMessage());
                throw new RuntimeException("退款失败: " + result.getMessage());
            }

        } catch (Exception e) {
            LogEsUtil.error("处理订单差错退款异常，订单id: " + orderId, e);
            throw e;  // 抛出异常触发事务回滚
        }
    }
}
