package com.ruoyi.system.constant;

/**
 * @author a1152
 */
//服务器状态
public interface ResourcesStatus {

    //正常
    public static final String NORMAL = "NORMAL";

    //服务器下线
    public static final String OFFLINE = "OFFLINE";

    //代理异常
    public static final String PROXY_ERROR = "PROXY_ERROR";

    //代理进程下线
    public static final String PROXY_PROCESS_OFFLINE = "PROXY_PROCESS_OFFLINE";

    //待获取通知数据
    public static final String WAIT_NOTICE_DATA = "WAIT_NOTICE_DATA";

}
