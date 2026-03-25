package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderByRenewalDto;
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
    Result orderIsPay(String orderId,Boolean isBalance);


    /**
     * [订单取消新版（不使用消息队列）]
     * @author 陈湘岳 2025/10/17
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result cancelOrderNew(String orderId);


    /**
     * [根据订单id获取支付二维码]
     * @author 陈湘岳 2025/10/24
     * @param orderId 订单id
     * @param payaType 支付方式，可选支付宝或微信
     * @return com.ruoyi.system.http.Result
     **/
    Result getQrCode(String orderId,String payaType);

    /**
     * [资源续费订单创建]
     * @author 陈湘岳 2025/10/28
     * @param orderByRenewalDto 创建参数
     * @return com.ruoyi.system.http.Result
     **/
    Result createOrderByRenewal(OrderByRenewalDto orderByRenewalDto);

    /**
     * [续费订单支付]
     * @author 陈湘岳 2025/10/29
     * @param orderId 订单id
     * @param isBalance 是否使用余额
     * @return com.ruoyi.system.http.Result
     **/
    Result renewalOrderIsPay(String orderId, Boolean isBalance);


    /**
     * [获取续费订单信息]
     * @author 陈湘岳 2025/11/14
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result getRenewalOrderInfo(String orderId);

    /**
     * [查询订单详情]
     * @author 陈湘岳 2025/11/19
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result getOrderDetailById(String orderId);


    /**
     * [续费订单取消]
     * @author 陈湘岳 2026/1/19
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result cancelOrderRenewal(String orderId);

    /**
     * [分页查询订单-客服]
     * @author 陈湘岳 2026/2/26
     * @param orderDto 查询参数
     * @return com.ruoyi.system.http.Result
     **/
    Result pageQueryService(OrderDto orderDto);

    /**
     * [定时关闭超时未支付订单]
     * 查询所有 WAIT_PAY 且下单时间超过30分钟的订单，异步并行关单
     * @author 陈湘岳
     * @return int 本次关闭的订单数量
     **/
    int autoCloseTimeoutOrders();
}
