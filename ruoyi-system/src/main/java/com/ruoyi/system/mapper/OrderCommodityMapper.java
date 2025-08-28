package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderCommodityDto;
import com.ruoyi.system.domain.entity.Order;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.OrderCommodity;

/**
 * 订单商品(OrderCommodity)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-15 10:01:36
 */
@Mapper
@Repository
public interface OrderCommodityMapper extends BaseMapper<OrderCommodity> {

    /**
     * [通过]
     * @author 陈湘岳 2025/8/20
     * @param id
     * @return com.ruoyi.system.domain.entity.OrderCommodity
     **/
    OrderCommodity queryById(@Param("id") String id);


    /**
     * [通过订单id查询订单关联商品信息]
     * @author 陈湘岳 2025/8/20
     * @param orderId
     * @return com.ruoyi.system.domain.entity.OrderCommodity
     **/
    OrderByCommodityDto findCommodityByOrderId(@Param("orderId") String orderId);
}
