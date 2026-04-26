package com.ruoyi.system.service;

/**
 * 资源到期通知服务接口
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/11
 */
public interface IResourceExpireNotifyService {

    /**
     * [处理资源到期通知]
     * 查询3天内到期且状态为WAIT_NOTIFY的资源,执行自动续费或发送邮件提醒
     *
     * @author 陈湘岳 2026/4/11
     * @return int 处理成功的资源数量
     **/
    int processExpireNotify();
}
