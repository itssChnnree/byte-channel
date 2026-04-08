package com.ruoyi.system.job;

import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.ProfitFlow;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.pay.application.dto.request.OrderRefundRequest;
import com.ruoyi.system.pay.application.service.PayOrderApplicationService;
import com.ruoyi.system.pay.application.vo.RefundResultVo;
import com.ruoyi.system.pay.domain.valueobject.RefundMode;
import com.ruoyi.system.service.IOrderStatusTimelineService;
import com.ruoyi.system.service.IProfitFlowService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 退款处理定时任务
 * 查询状态为 WAIT_REFUND 的订单，执行退款操作
 *
 * @author 陈湘岳
 * @date 2026/3/31
 */
@Slf4j
@Component
public class RefundProcessJob {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private PayOrderApplicationService payOrderApplicationService;

    @Resource
    private IOrderStatusTimelineService orderStatusTimelineService;

    @Resource
    private ThreadPoolTaskExecutor errorRefundExecutor;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Resource
    private IProfitFlowService profitFlowService;

    /**
     * 每批处理的订单数量
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 单个订单处理超时时间（秒）
     */
    private static final int ORDER_TIMEOUT_SECONDS = 30;

    /**
     * 退款处理定时任务
     * 在XXL-JOB调度中心配置：
     * - JobHandler: refundProcessJob
     * - 执行策略: 根据业务需求配置（如每5分钟执行一次）
     * - 任务参数: 可选，传入limit值（默认100）
     */
    @XxlJob("refundProcessJob")
    public void execute() {
        String jobParam = XxlJobHelper.getJobParam();
        int limit = BATCH_SIZE;
        String traceId = TraceIdContext.generateTraceId();
        TraceIdContext.initContext(traceId);
        if (jobParam != null && !jobParam.trim().isEmpty()) {
            try {
                limit = Integer.parseInt(jobParam.trim());
            } catch (NumberFormatException e) {
                LogEsUtil.warn("退款处理任务参数解析失败，使用默认值: " + BATCH_SIZE);
            }
        }

        LogEsUtil.info("开始执行退款处理定时任务，查询数量限制: " + limit);
        XxlJobHelper.log("开始执行退款处理定时任务，查询数量限制: " + limit);

        try {
            // 1. 查询待退款的订单（WAIT_REFUND状态）
            List<Order> orders = orderMapper.findWaitRefundOrders(limit);

            if (orders == null || orders.isEmpty()) {
                LogEsUtil.info("没有需要退款的订单");
                XxlJobHelper.log("没有需要退款的订单");
                XxlJobHelper.handleSuccess("没有需要处理的订单");
                return;
            }

            LogEsUtil.info("查询到 " + orders.size() + " 个需要退款的订单");
            XxlJobHelper.log("查询到 " + orders.size() + " 个需要退款的订单");

            // 2. 使用多线程并行处理
            AtomicInteger successCount = new AtomicInteger(0);
            AtomicInteger failCount = new AtomicInteger(0);
            CountDownLatch latch = new CountDownLatch(orders.size());

            for (Order order : orders) {
                CompletableFuture.runAsync(() -> {
                    try {
                        // 每个订单在独立事务中处理
                        transactionTemplate.execute(status -> {
                            processRefund(order);
                            return null;
                        });
                        successCount.incrementAndGet();
                    } catch (Exception e) {
                        LogEsUtil.error("订单退款失败，订单id: " + order.getId(), e);
                        failCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                }, errorRefundExecutor);
            }

            // 3. 等待所有任务完成（带超时）
            boolean completed = latch.await(ORDER_TIMEOUT_SECONDS * orders.size(), TimeUnit.SECONDS);
            if (!completed) {
                LogEsUtil.warn("退款处理任务超时，部分订单可能未处理完成");
                XxlJobHelper.log("退款处理任务超时，部分订单可能未处理完成");
            }

            String resultMsg = String.format("退款处理任务执行完成，成功: %d, 失败: %d, 总计: %d",
                    successCount.get(), failCount.get(), orders.size());
            LogEsUtil.info(resultMsg);
            XxlJobHelper.log(resultMsg);
            XxlJobHelper.handleSuccess(resultMsg);

        } catch (Exception e) {
            LogEsUtil.error("退款处理定时任务执行失败", e);
            XxlJobHelper.log("退款处理定时任务执行失败: " + e.getMessage());
            XxlJobHelper.handleFail("退款处理任务执行失败: " + e.getMessage());
        }
    }

    /**
     * 处理单个订单的退款
     * 使用独立事务，确保每个订单的退款操作原子性
     *
     * @param order 订单对象
     */
    public void processRefund(Order order) {
        String orderId = order.getId();
        LogEsUtil.info("开始处理订单退款，订单id: " + orderId);

        try {
            // 构建退款请求
            OrderRefundRequest request = new OrderRefundRequest();
            request.setOrderId(orderId);
            // 用户退款模式（退所有已支付渠道）
            request.setRefundMode(RefundMode.USER_REFUND.getCode());
            request.setRefundToBalance(needToRefundToBalance(order));
            request.setRefundReason("定时任务：订单退款");

            // 调用退款服务
            RefundResultVo result = payOrderApplicationService.refundByOrder(request);

            if (result.getSuccess()) {
                // 退款成功，更新订单状态为已退款
                orderMapper.updateStatusById(orderId, OrderStatus.REFUND_SUCCESS);
                // 更新时间线
                orderStatusTimelineService.setRefundedSuccessTime(orderId);
                //扣减利润流水
                reduceProfitFlow(order);
                LogEsUtil.info("订单退款成功，订单id: " + orderId +
                        ", 退款金额: " + result.getTotalRefundAmount());
            } else {
                LogEsUtil.warn("订单退款失败，订单id: " + orderId +
                        ", 原因: " + result.getMessage());
                throw new RuntimeException("退款失败: " + result.getMessage());
            }

        } catch (Exception e) {
            LogEsUtil.error("处理订单退款异常，订单id: " + orderId, e);
            // 抛出异常触发事务回滚
            throw e;
        }
    }

    //判断支付方式，若为微信支付或支付宝支付，扣减利润流水
    private void reduceProfitFlow(Order order) {
        LogEsUtil.info("扣减利润流水："+order.getId()+",利润类型："+order.getOrderType()+"，利润金额："+order.getAmount());
        profitFlowService.deleteBySourceTypeAndId(order.getOrderType(), order.getId());
    }

    private Boolean needToRefundToBalance(Order order) {
        if (order.getRefoundToBalance() != null && order.getRefoundToBalance() == 1) {
            return true;
        }
        return false;
    }
}
