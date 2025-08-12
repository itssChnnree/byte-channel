package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.http.Result;

/**
 * 订单表(Order)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
public interface IOrderService{




    /**
     * [直接从商品创建订单]
     * @author 陈湘岳 2025/8/10
     * @param orderByCommodityDto 创建参数
     * @return com.ruoyi.system.http.Result
     **/
    Result createOrderByCommodity(OrderByCommodityDto orderByCommodityDto);
}
