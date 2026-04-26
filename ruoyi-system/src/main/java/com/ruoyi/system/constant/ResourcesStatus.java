package com.ruoyi.system.constant;

/**
 * @author a1152
 */
//服务器状态
public interface ResourcesStatus {

    //正常
     String NORMAL = "NORMAL";

    //异常
     String ERROR = "ERROR";

    //待获取通知数据
     String WAIT_NOTICE_DATA = "WAIT_NOTICE_DATA";

    //待检测
     String WAIT_CHECK = "WAIT_CHECK";

     //续费待通知
    String WAIT_NOTIFY = "WAIT_NOTIFY";

    //已通知
    String NOTIFIED = "NOTIFIED";

}
