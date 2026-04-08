package com.ruoyi.system.job;

import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.service.IServerResourcesService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

//资源清理
@Slf4j
@Component
public class ResourceExpireCleanJob {

    @Resource
    private IServerResourcesService serverResourcesService;

    @XxlJob("resourceExpireCleanJob")
    public int execute() {
        String traceId = TraceIdContext.generateTraceId();
        TraceIdContext.initContext(traceId);
        XxlJobHelper.log("Start resource expire clean job");
        LogEsUtil.info("Resource expire clean job started");

        try {
            int affectedCount = serverResourcesService.cleanExpiredResources();

            String message = "Resource expire clean job completed, cleaned count: " + affectedCount;
            LogEsUtil.info(message);
            XxlJobHelper.handleSuccess(message);

            return affectedCount;
        } catch (Exception e) {
            String errorMsg = "Resource expire clean job failed: " + e.getMessage();
            LogEsUtil.error(errorMsg, e);
            XxlJobHelper.handleFail(errorMsg);
            return 0;
        }
    }
}
