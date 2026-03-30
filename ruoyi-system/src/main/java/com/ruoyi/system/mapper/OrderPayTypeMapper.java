package com.ruoyi.system.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.OrderPayType;

/**
 * 订单支付方式表(OrderPayType
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-23 21:16:06
 */
@Mapper
@Repository
public interface OrderPayTypeMapper extends BaseMapper<OrderPayType> {

    /**
     * [加锁查询订单]
     * @author 陈湘岳 2026/3/23
     * @param orderId 订单id
     * @return com.ruoyi.system.domain.entity.OrderPayType
     **/
    OrderPayType selectByIdForUpdate(@Param("orderId") String orderId);

    /**
     * [重试次数+1]
     * @author 陈湘岳 2026/3/23
     * @param orderId 订单id
     * @return com.ruoyi.system.domain.entity.OrderPayType
     **/
    int addRetryCount(@Param("orderId")String orderId);

    /**
     * [完成差错退款]
     * @author 陈湘岳 2026/3/24
     * @param orderId 订单id
     * @return int
     **/
    int updateIsCheckInt(@Param("orderId")String orderId);
}
