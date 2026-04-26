package com.ruoyi.system.job;

import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.service.IResourceExpireNotifyService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 资源到期通知定时任务
 * 遵循项目XXL-JOB标准模式：TraceIdContext + XxlJobHelper
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/11
 */
@Component
@Slf4j
public class ResourceExpireNotifyJob {

    @Resource(name = "resourceExpireNotifyService")
    private IResourceExpireNotifyService resourceExpireNotifyService;

    /**
     * 资源到期通知任务
     * 每天执行一次，查询3天内到期且状态为WAIT_NOTIFY的资源
     */
    @XxlJob("resourceExpireNotifyJob")
    public int execute() {
        String traceId = TraceIdContext.generateTraceId();
        TraceIdContext.initContext(traceId);
        XxlJobHelper.log("Start resource expire notify job");
        LogEsUtil.info("========== 资源到期通知任务开始 ==========");

        try {
            int count = resourceExpireNotifyService.processExpireNotify();

            String message = "资源到期通知任务完成，处理资源数量：" + count;
            LogEsUtil.info(message);
            XxlJobHelper.handleSuccess(message);

            return count;
        } catch (Exception e) {
            String errorMsg = "资源到期通知任务执行异常：" + e.getMessage();
            LogEsUtil.error(errorMsg, e);
            XxlJobHelper.handleFail(errorMsg);

            return 0;
        }
    }
}
