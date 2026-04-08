package com.ruoyi.system.job;

import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.service.IOrderService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 订单超时检测定时任务
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class OrderTimeoutCheckJob {

    @Resource
    private IOrderService orderService;

    /**
     * 订单超时检测任务
     * 在XXL-JOB调度中心配置：
     * - JobHandler: orderTimeoutCheckJob
     * - 执行策略: 根据业务需求配置（如每1分钟执行一次）
     */
    @XxlJob("orderTimeoutCheckJob")
    public void execute() {
        String traceId = TraceIdContext.generateTraceId();
        TraceIdContext.initContext(traceId);
        LogEsUtil.info("订单超时关闭执行完成");
        try {
            orderService.autoCloseTimeoutOrders();

            LogEsUtil.info("订单超时关闭执行完成");
            XxlJobHelper.handleSuccess("订单超时检测成功");
        } catch (Exception e) {
            LogEsUtil.error("订单超时检测任务执行失败", e);
            XxlJobHelper.handleFail("订单超时检测失败: " + e.getMessage());
        }
    }
}
