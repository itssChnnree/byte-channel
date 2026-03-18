package com.ruoyi.system.service;

import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.vo.YiPayResponse;
import com.ruoyi.system.http.Result;

public interface IOrderBaseService {

    /**
     * 订单状态校验
     *
     * @param orderId 订单id
     * @param order 订单
     * @return
     */
    Result<?> validStatus(String orderId, Order order);


    /**
     * 订单状态转换
     *
     * @param status
     * @return
     */
    String orderStatusConvert(String status);

    /**
     * 获取订单信息(支付状态)
     *
     * @param order
     * @return
     */
    YiPayResponse getOrderInfo(Order order);

    /**
     * 添加利润流水
     *
     * @param order
     * @param desc
     */
    void addProfit(Order order,String desc);

}
