package com.ruoyi.system.job;

import com.ruoyi.system.service.IResourceAllocationService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
//资源分配定时任务
@Slf4j
@Component
public class ResourceAllocationJob {

    @Resource
    private IResourceAllocationService resourceAllocationService;

    @XxlJob("resourceAllocationJob")
    public void execute() {
        XxlJobHelper.log("Start executing resource allocation job");
        LogEsUtil.info("Resource allocation job started");

        try {
            int successCount = resourceAllocationService.execute();

            String message = String.format("Resource allocation job completed, successfully allocated %d orders", successCount);
            LogEsUtil.info(message);
            XxlJobHelper.handleSuccess(message);

        } catch (Exception e) {
            String errorMsg = "Resource allocation job failed: " + e.getMessage();
            LogEsUtil.error(errorMsg, e);
            XxlJobHelper.handleFail(errorMsg);
        }
    }
}
