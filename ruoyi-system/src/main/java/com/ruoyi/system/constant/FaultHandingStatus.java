package com.ruoyi.system.constant;

/**
 * [故障处理流程状态]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/2
 */
public interface FaultHandingStatus {

    //待处理
     String PENDING = "PENDING";

    //处理中
    String PROCESSING = "PROCESSING";

    //处理完成
    String RESOLVED = "RESOLVED";

    //已忽略
    String IGNORED = "IGNORED";

    //已自愈
    String SELF_HEALING = "SELF_HEALING";
}
