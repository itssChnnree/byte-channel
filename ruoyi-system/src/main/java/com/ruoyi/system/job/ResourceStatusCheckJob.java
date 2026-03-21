package com.ruoyi.system.job;

import com.ruoyi.system.service.IServerResourcesService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 资源状态检测定时任务
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class ResourceStatusCheckJob {

    @Resource
    IServerResourcesService iServerResourcesService;

    /**
     * 资源状态检测任务
     * 在XXL-JOB调度中心配置：
     * - JobHandler: resourceStatusCheckJob
     * - 执行策略: 根据业务需求配置（如每5分钟执行一次）
     */
    @XxlJob("resourceStatusCheckJob")
    public void execute() {
        XxlJobHelper.log("开始执行资源状态检测任务");
        try {
            iServerResourcesService.resourceDetectionTask();
            LogEsUtil.info("资源状态检测任务执行完成");
            XxlJobHelper.handleSuccess("资源状态检测成功");
        } catch (Exception e) {
            LogEsUtil.error("资源状态检测任务执行失败", e);
            XxlJobHelper.handleFail("资源状态检测任务执行失败: " + e.getMessage());
        }
    }
}
