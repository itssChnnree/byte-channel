package com.ruoyi.system.strategy.NotificationUser;

import cn.hutool.extra.spring.SpringUtil;


/**
 * [用户通知上下文类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/6/24
 */
public class NotificationContext {

    private NotificationStrategy notificationStrategy;

    public NotificationContext(String notificationType) {
        notificationStrategy = SpringUtil.getBean(notificationType);
    }



}
