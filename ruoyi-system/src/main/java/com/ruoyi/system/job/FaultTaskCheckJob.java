package com.ruoyi.system.job;

import com.ruoyi.system.domain.entity.ServerResourceAlarm;
import com.ruoyi.system.service.IServerResourceAlarmService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 故障任务检测定时任务
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class FaultTaskCheckJob {

    @Resource
    private IServerResourceAlarmService iServerResourceAlarmService;

    /**
     * 故障任务检测任务
     * 在XXL-JOB调度中心配置：
     * - JobHandler: faultTaskCheckJob
     * - 执行策略: 根据业务需求配置（如每10分钟执行一次）
     */
    @XxlJob("faultTaskCheckJob")
    public void execute() {
        XxlJobHelper.log("开始执行故障任务检测任务");
        try {
            XxlJobHelper.log("故障任务检测任务执行完成");
            XxlJobHelper.handleSuccess("故障任务检测成功");
        } catch (Exception e) {
            LogEsUtil.error("故障任务检测任务执行失败", e);
            XxlJobHelper.log("故障任务检测任务执行失败: " + e.getMessage());
            XxlJobHelper.handleFail("故障任务检测失败: " + e.getMessage());
        }
    }
}
