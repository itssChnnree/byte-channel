package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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

}
