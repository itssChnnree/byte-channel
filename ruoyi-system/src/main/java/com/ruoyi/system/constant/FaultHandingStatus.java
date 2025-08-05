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
     String WAIT_HANDLE = "WAIT_HANDLE";

    //处理中
    String HANDLING = "HANDLING";

    //处理完成
    String HANDLE_COMPLETE = "HANDLE_COMPLETE";
}
