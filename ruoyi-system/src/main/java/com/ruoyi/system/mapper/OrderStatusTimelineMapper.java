package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.vo.OrderStatusTimelineVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.OrderStatusTimeline;

/**
 * 订单状态时间线记录表（一行记录所有变更时间）(OrderStatusTimeline
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-19 21:20:36
 */
@Mapper
@Repository
public interface OrderStatusTimelineMapper extends BaseMapper<OrderStatusTimeline> {


    @Select("select * from order_status_timeline where order_id = #{orderId}")
    OrderStatusTimeline selectByOrderIde(@Param("orderId") String orderId);

    @Select("select * from order_status_timeline where order_id = #{orderId}")
    OrderStatusTimelineVo selectVoByOrderId(@Param("orderId") String orderId);
}
