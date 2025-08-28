package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.OrderCommodityResources;
import com.ruoyi.system.domain.entity.ServerResources;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 订单商品资源(OrderCommodityResources)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:07
 */
@Mapper
@Repository
public interface OrderCommodityResourcesMapper extends BaseMapper<OrderCommodityResources> {


    /**
     * [通过订单id查询资源]
     * @author 陈湘岳 2025/8/21
     * @param orderId 订单id查询资源
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    List<OrderCommodityResources> findByOrderId(@Param("orderId")String orderId);

    /**
     * [查询续费成功的资源]
     * @author 陈湘岳 2025/8/21
     * @param orderId 订单id查询资源
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    List<OrderCommodityResources> findByOrderIdAndStatus(@Param("orderId")String orderId,@Param("status")String status);


    /**
     * [修改订单商品资源状态]
     * @author 陈湘岳 2025/8/21
     * @param orderId 订单id查询资源
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    int updateStatus(@Param("orderId")String orderId,@Param("status")String status);
}
