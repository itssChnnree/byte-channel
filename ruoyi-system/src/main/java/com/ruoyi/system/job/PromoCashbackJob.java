package com.ruoyi.system.job;

import com.ruoyi.system.config.TraceIdContext;
import com.ruoyi.system.service.IPromoCashbackService;
import com.ruoyi.system.util.LogEsUtil;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 推广返现定时任务
 * 遵循项目XXL-JOB标准模式：TraceIdContext + XxlJobHelper
 *
 * @author 陈湘岳
 * @date 2026/4/7
 */
@Slf4j
@Component
public class PromoCashbackJob {

    @Resource
    private IPromoCashbackService promoCashbackService;

    /**
     * 推广返现任务
     * 查询待确认且订单完成超过24小时的推广记录，为推荐人增加返现金额
     */
    @XxlJob("promoCashbackJob")
    public int execute() {
        String traceId = TraceIdContext.generateTraceId();
        TraceIdContext.initContext(traceId);
        XxlJobHelper.log("Start promo cashback job");
        LogEsUtil.info("========== 推广返现任务开始 ==========");

        try {
            int successCount = promoCashbackService.execute();

            String message = "推广返现任务完成，成功返现数量：" + successCount;
            LogEsUtil.info(message);
            XxlJobHelper.handleSuccess(message);

            return successCount;
        } catch (Exception e) {
            String errorMsg = "推广返现任务执行异常：" + e.getMessage();
            LogEsUtil.error(errorMsg, e);
            XxlJobHelper.handleFail(errorMsg);
            return 0;
        }
    }
}
