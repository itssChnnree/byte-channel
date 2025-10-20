package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderByShoppingCartDto;
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


    /**
     * [分页查询订单]
     * @author 陈湘岳 2025/8/14
     * @param orderDto 分页查询订单
     * @return com.ruoyi.system.http.Result
     **/
    Result pageQuery(OrderDto orderDto);


    /**
     * [消息队列测试]
     * @author 陈湘岳 2025/8/14
     * @param
     * @return java.lang.String
     **/
    String test();


    /**
     * [取消订单]
     * @author 陈湘岳 2025/8/24
     * @param orderId
     * @return com.ruoyi.system.http.Result
     **/
    Result cancelOrder(String orderId);


    /**
     * [计算价格]
     * @author 陈湘岳 2025/8/24
     * @param orderByCommodityDto
     * @return com.ruoyi.system.http.Result
     **/
    Result calculatePrice(OrderByCommodityDto orderByCommodityDto);

    /**
     * [获取订单信息]
     * @author 陈湘岳 2025/8/24
     * @param orderId
     * @return com.ruoyi.system.http.Result
     **/
    Result getOrderInfo(String orderId);


    /**
     * [获取订单状态]
     * @author 陈湘岳 2025/10/16
     * @param orderId
     * @return com.ruoyi.system.http.Result
     **/
    Result getOrderStatus(String orderId);

    /**
     * [用户点击订单已支付]
     * @author 陈湘岳 2025/10/16
     * @param orderId
     * @return com.ruoyi.system.http.Result
     **/
    Result orderIsPay(String orderId);


    /**
     * [订单取消新版（不使用消息队列）]
     * @author 陈湘岳 2025/10/17
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result cancelOrderNew(String orderId,String status);
}
