package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.OrderStatusTimelineDto;
import com.ruoyi.system.mapper.OrderStatusTimelineMapper;
import com.ruoyi.system.service.IOrderStatusTimelineService;
import com.ruoyi.system.domain.entity.OrderStatusTimeline;
import com.ruoyi.system.domain.vo.OrderStatusTimelineVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 订单状态时间线记录表（一行记录所有变更时间）(OrderStatusTimeline)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-19 21:20:37
 */
@Service("orderStatusTimelineService")
public class OrderStatusTimelineServiceImpl implements IOrderStatusTimelineService {


    @Resource
    private OrderStatusTimelineMapper orderStatusTimelineMapper;

    /**
     * [创建订单及待支付时间线]
     *
     * @param orderId 订单id
     * @return void
     * @author 陈湘岳 2025/11/19
     **/
    @Override
    public void createOrderAndTimeline(String orderId) {
        OrderStatusTimeline orderStatusTimeline = new OrderStatusTimeline();
        orderStatusTimeline.setOrderId(orderId);
        orderStatusTimeline.setOrderCreatedTime(new Date());
        orderStatusTimeline.setWaitPayTime(new Date());
        orderStatusTimelineMapper.insert(orderStatusTimeline);
    }

    /**
     * [设置超时系统字段取消时间线]
     *
     * @param orderId 订单id
     * @return void
     * @author 陈湘岳 2025/11/19
     **/
    @Override
    public void setTimeoutCanceledTime(String orderId) {
        OrderStatusTimeline orderStatusTimeline = orderStatusTimelineMapper.selectByOrderIde(orderId);
        if(ObjectUtil.isNull(orderStatusTimeline)){
            throw new RuntimeException("订单时间线不存在");
        }
        orderStatusTimeline.setTimeoutCanceledTime(new Date());
        orderStatusTimelineMapper.updateById(orderStatusTimeline);
    }

    /**
     * [设置用户支付及待分配资源时间线]
     *
     * @param orderId 订单id
     * @return void
     * @author 陈湘岳 2025/11/19
     **/
    @Override
    public void setUserPayAndWaitAllocationTime(String orderId) {
        OrderStatusTimeline orderStatusTimeline = orderStatusTimelineMapper.selectByOrderIde(orderId);
        if(ObjectUtil.isNull(orderStatusTimeline)){
            throw new RuntimeException("订单时间线不存在");
        }
        orderStatusTimeline.setUserPayTime(new Date());
        orderStatusTimeline.setWaitAllocationTime(new Date());
        orderStatusTimelineMapper.updateById(orderStatusTimeline);
    }

    /**
     * [设置已完成时间线]
     *
     * @param orderId 订单id
     * @return void
     * @author 陈湘岳 2025/11/19
     **/
    @Override
    public void setCompletedTime(String orderId) {
        OrderStatusTimeline orderStatusTimeline = orderStatusTimelineMapper.selectByOrderIde(orderId);
        if(ObjectUtil.isNull(orderStatusTimeline)){
            throw new RuntimeException("订单时间线不存在");
        }
        orderStatusTimeline.setCompletedTime(new Date());
        orderStatusTimelineMapper.updateById(orderStatusTimeline);
    }

    /**
     * [设置用户取消时间线及待退款时间线]
     *
     * @param orderId 订单id
     * @return void
     * @author 陈湘岳 2025/11/19
     **/
    @Override
    public void setUserCanceledAndWaitRefundTime(String orderId) {
        OrderStatusTimeline orderStatusTimeline = orderStatusTimelineMapper.selectByOrderIde(orderId);
        if(ObjectUtil.isNull(orderStatusTimeline)){
            throw new RuntimeException("订单时间线不存在");
        }
        orderStatusTimeline.setUserCanceledTime(new Date());
        orderStatusTimeline.setWaitRefundTime(new Date());
        orderStatusTimelineMapper.updateById(orderStatusTimeline);
    }

    /**
     * [设置已退款时间线]
     *
     * @param orderId 订单id
     * @return void
     * @author 陈湘岳 2025/11/19
     **/
    @Override
    public void setRefundedSuccessTime(String orderId) {
        OrderStatusTimeline orderStatusTimeline = orderStatusTimelineMapper.selectByOrderIde(orderId);
        if(ObjectUtil.isNull(orderStatusTimeline)){
            throw new RuntimeException("订单时间线不存在");
        }
        orderStatusTimeline.setRefundSuccessTime(new Date());
        orderStatusTimelineMapper.updateById(orderStatusTimeline);
    }

    /**
     * [查询订单时间线]
     *
     * @param orderId 订单id
     * @return
     * @author 陈湘岳 2025/11/20
     **/
    @Override
    public OrderStatusTimelineVo getByOrderId(String orderId) {
        return orderStatusTimelineMapper.selectVoByOrderId(orderId);
    }
}
