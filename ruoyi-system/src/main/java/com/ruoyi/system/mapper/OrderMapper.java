package com.ruoyi.system.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.vo.OrderDetailVo;
import com.ruoyi.system.domain.vo.OrderInfoVo;
import com.ruoyi.system.domain.vo.OrderVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 订单表(Order)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@Mapper
@Repository
public interface OrderMapper extends BaseMapper<Order> {


    /**
     * [分页查询订单状态]
     * @author 陈湘岳 2025/11/3
     * @param orderDto 订单参数
     * @param userId 用户id
     * @return java.util.List<com.ruoyi.system.domain.vo.OrderVo>
     **/
    List<OrderVo> queryPage(@Param("dto") OrderDto orderDto, @Param("userId") String userId);


    /**
     * [通过订单id查询订单]
     * @author 陈湘岳 2025/8/19
     * @param id 订单id
     * @return com.ruoyi.system.domain.entity.Order
     **/
    Order queryById(@Param("id") String id);


    /**
     * [通过订单id查询订单]
     * @author 陈湘岳 2025/8/19
     * @param id 订单id
     * @return com.ruoyi.system.domain.entity.Order
     **/
    Order queryByIdForUpdate(@Param("id") String id);


    /**
     * [根据订单id修改订单状态]
     * @author 陈湘岳 2025/10/16
     * @param id 订单id
     * @param status 订单状态
     * @return int
     **/
    int updateStatusById(@Param("id") String id, @Param("status") String status);

    /**
     * [将指定id的订单状态修改为待退款]
     * @author 陈湘岳 2025/10/16
     * @param id 订单id
     * @param status 订单状态
     * @return int
     **/
    int refoundById(@Param("id") String id,
                    @Param("status") String status,
                    @Param("refound_to_balance")Integer refoundToBalance);
    
    
    /**
     * 查询订单信息
     * @author 陈湘岳 2025/9/3
     * @param id 订单id
     * @param userId 用户id
     * @return com.ruoyi.system.domain.vo.OrderInfoVo
     **/
    OrderInfoVo getOrderInfoById(@Param("id") String id, @Param("userId") String userId);

    /**
     * [查询已完成或已取消且需要差错退款的订单]
     * 查询状态为 COMPLETED 且存在微信或支付宝渠道的订单
     * @author 陈湘岳
     * @param limit 查询数量限制
     * @return java.util.List<com.ruoyi.system.domain.entity.Order>
     **/
    List<Order> findCompletedOrdersForErrorRefund(@Param("limit") int limit);


    /**
     * 查询续费订单信息
     * @author 陈湘岳 2025/9/3
     * @param id 订单id
     * @param userId 用户id
     * @return com.ruoyi.system.domain.vo.OrderInfoVo
     **/
    OrderInfoVo getRenewalOrderInfoById(@Param("id") String id, @Param("userId") String userId);

    /**
     * [查询订单详情]
     * @author 陈湘岳 2025/11/19
     * @param orderId
     * @return com.ruoyi.system.domain.vo.OrderDetailVo
     **/
    OrderDetailVo getOrderDetailById(String orderId);

    /**
     * [查询指定订单类型的超时未支付订单]
     * 查询状态为 WAIT_PAY 且 order_time 距今超过指定分钟数的订单
     * @author 陈湘岳
     * @param minutes 超时分钟数
     * @param orderType 订单类型
     * @return java.util.List<com.ruoyi.system.domain.entity.Order>
     **/
    List<Order> findTimeoutWaitPayOrders(@Param("minutes") int minutes,
                                         @Param("orderType")String orderType);

    /**
     * [查询待分配资源的新购订单]
     * 查询状态为 WAIT_ALLOCATION_RESOURCES 且订单类型为新购的订单
     * @author 陈湘岳 2026/3/23
     * @return java.util.List<com.ruoyi.system.domain.entity.Order>
     **/
    List<Order> selectWaitAllocationOrders();

    /**
     * [查询待退款订单]
     * 查询状态为 WAIT_REFUND 的订单
     * @author 陈湘岳
     * @param limit 查询数量限制
     * @return java.util.List<com.ruoyi.system.domain.entity.Order>
     **/
    List<Order> findWaitRefundOrders(@Param("limit") int limit);

    /**
     * [通过id上锁查询订单]
     * @author 陈湘岳 2026/4/7
     * @param id 订单id
     * @return com.ruoyi.system.domain.entity.Order
     **/
    Order selectByIdForUpdate(@Param("id") String id);
}
