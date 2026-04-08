package com.ruoyi.system.job;

import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.domain.entity.Ticket;
import com.ruoyi.system.service.ITicketService;
import com.ruoyi.system.service.impl.TicketServiceImpl;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 定时关闭工单任务
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class WorkOrderCloseJob {

    @Resource
    private ITicketService ticketService;

    /**
     * 定时关闭工单任务
     * 在XXL-JOB调度中心配置：
     * - JobHandler: workOrderCloseJob
     * - 执行策略: 根据业务需求配置（如每天凌晨执行）
     */
    @XxlJob("workOrderCloseJob")
    public void execute() {
        String traceId = TraceIdContext.generateTraceId();
        TraceIdContext.initContext(traceId);
        LogEsUtil.info("开始执行工单关闭任务");
        try {
            ticketService.autoCloseTimeoutTickets();
            LogEsUtil.info("工单关闭任务执行完成");
            XxlJobHelper.handleSuccess("工单关闭成功");
        } catch (Exception e) {
            LogEsUtil.error("工单关闭任务执行失败", e);
            XxlJobHelper.handleFail("工单关闭失败: " + e.getMessage());
        }
    }
}
