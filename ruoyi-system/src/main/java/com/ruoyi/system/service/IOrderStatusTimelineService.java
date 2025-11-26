package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.OrderStatusTimelineDto;
import com.ruoyi.system.domain.entity.OrderStatusTimeline;
import com.ruoyi.system.domain.vo.OrderStatusTimelineVo;


/**
 * 订单状态时间线记录表（一行记录所有变更时间）(OrderStatusTimeline)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-19 21:20:36
 */
public interface IOrderStatusTimelineService {


    /**
     * [创建订单及待支付时间线]
     * @author 陈湘岳 2025/11/19
     * @param orderId 订单id
     * @return void
     **/
    void createOrderAndTimeline(String orderId);


    /**
     * [设置超时系统字段取消时间线]
     * @author 陈湘岳 2025/11/19
     * @param orderId 订单id
     * @return void
     **/
    void setTimeoutCanceledTime(String orderId);


    /**
     * [设置用户支付及待分配资源时间线]
     * @author 陈湘岳 2025/11/19
     * @param orderId 订单id
     * @return void
     **/
    void setUserPayAndWaitAllocationTime(String orderId);


    /**
     * [设置已完成时间线]
     * @author 陈湘岳 2025/11/19
     * @param orderId 订单id
     * @return void
     **/
    void setCompletedTime(String orderId);


    /**
     * [设置用户取消时间线及待退款时间线]
     * @author 陈湘岳 2025/11/19
     * @param orderId 订单id
     * @return void
     **/
    void setUserCanceledAndWaitRefundTime(String orderId);


    /**
     * [设置已退款时间线]
     * @author 陈湘岳 2025/11/19
     * @param orderId 订单id
     * @return void
     **/
    void setRefundedSuccessTime(String orderId);


    /**
     * [查询订单时间线]
     * @author 陈湘岳 2025/11/20
     * @param orderId 订单id
     * @return
     **/
    OrderStatusTimelineVo getByOrderId(String orderId);

}
