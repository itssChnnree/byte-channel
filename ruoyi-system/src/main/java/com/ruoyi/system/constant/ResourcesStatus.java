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

    //服务器下线
     String OFFLINE = "OFFLINE";

    //代理异常
     String PROXY_ERROR = "PROXY_ERROR";

    //代理进程下线
     String PROXY_PROCESS_OFFLINE = "PROXY_PROCESS_OFFLINE";

    //待获取通知数据
     String WAIT_NOTICE_DATA = "WAIT_NOTICE_DATA";

    //待检测
     String WAIT_CHECK = "WAIT_CHECK";

}
