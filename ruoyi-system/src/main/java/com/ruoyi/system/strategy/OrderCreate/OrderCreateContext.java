package com.ruoyi.system.strategy.OrderCreate;

import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.system.strategy.tournamentNotificationAppointmentsUser.NotificationStrategy;


/**
 * [用户通知上下文类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/6/24
 */
public class OrderCreateContext {

    private OrderCreateStrategy orderCreateStrategy;

    public OrderCreateContext(String orderCreateType) {
        orderCreateStrategy = SpringUtil.getBean(orderCreateType);
    }



}
