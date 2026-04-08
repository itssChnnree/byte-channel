package com.ruoyi.system.job;

import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.service.IServerResourceAlarmService;
import com.ruoyi.system.service.IServerResourcesService;
import com.ruoyi.system.util.LogEsUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * 资源故障自愈定时任务
 * 查询所有resources_status为ERROR的资源，检测状态，若恢复正常则自愈
 *
 * @author ruoyi
 */
@Slf4j
@Component
public class ResourceErrorRecoveryJob {

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private IServerResourcesService iServerResourcesService;

    @Resource
    private IServerResourceAlarmService serverResourceAlarmService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource(name = "resourceDetectionExecutor")
    private Executor resourceDetectionExecutor;

    /**
     * 资源故障自愈任务
     * 在XXL-JOB调度中心配置：
     * - JobHandler: resourceErrorRecoveryJob
     * - 执行策略: 根据业务需求配置（如每2分钟执行一次）
     */
    @XxlJob("resourceErrorRecoveryJob")
    public void execute() {
        XxlJobHelper.log("开始执行资源故障自愈任务");
        String traceId = TraceIdContext.generateTraceId();
        TraceIdContext.initContext(traceId);
        LogEsUtil.info("开始执行资源故障自愈任务");


        try {
            // 查询所有状态为ERROR的资源
            List<ServerResources> errorResources = serverResourcesMapper.findByResourcesStatus("ERROR");

            if (CollectionUtils.isEmpty(errorResources)) {
                LogEsUtil.info("资源故障自愈任务：没有查询到状态为ERROR的资源");
                return;
            }

            LogEsUtil.info("资源故障自愈任务：共发现 " + errorResources.size() + " 个ERROR状态资源需要检测");

            Authentication authentication = SecurityUtils.getAuthentication();

            // 为每个ERROR资源创建异步检测任务
            errorResources.forEach(resource -> {
                CompletableFuture.runAsync(() -> {
                    transactionTemplate.execute(status -> {
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        detectAndRecoverResource(resource);
                        return null;
                    });
                }, resourceDetectionExecutor);
            });

            LogEsUtil.info("资源故障自愈任务已启动，共 " + errorResources.size() + " 个资源");
            XxlJobHelper.handleSuccess("资源故障自愈任务已启动，共 " + errorResources.size() + " 个资源，traceId为" + traceId);

        } catch (Exception e) {
            LogEsUtil.error("资源故障自愈任务执行失败", e);
            XxlJobHelper.handleFail("资源故障自愈任务执行失败: " + e.getMessage());
        }
    }

    /**
     * 检测单个ERROR资源，若恢复正常则自愈
     * @param resource 资源实体
     */
    private void detectAndRecoverResource(ServerResources resource) {
        String resourcesId = resource.getId();
        String resourcesIp = resource.getResourcesIp();

        try {
            LogEsUtil.info("开始检测ERROR资源：" + resourcesId + "，IP：" + resourcesIp);

            // 调用资源状态检测方法
            Result result = iServerResourcesService.getResourcesStatus(resourcesId);

            // 检测结果是否正常
            if (!ObjectUtils.isEmpty(result)
                    && !ObjectUtils.isEmpty(result.getData())
                    && "200".equals(result.getData().toString().trim())) {
                // 资源已恢复正常，执行自愈
                LogEsUtil.info("ERROR资源 " + resourcesId + " 已恢复正常，执行自愈");
                serverResourceAlarmService.repairResourceAlarm(resourcesId, "SELF_HEALING");
                LogEsUtil.info("ERROR资源 " + resourcesId + " 自愈完成");
            } else {
                LogEsUtil.info("ERROR资源 " + resourcesId + " 仍未恢复，继续监控");
            }

        } catch (Exception e) {
            LogEsUtil.error("检测ERROR资源 " + resourcesId + " 时发生异常：" + e.getMessage(), e);
        }
    }
}
