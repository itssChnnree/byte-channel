package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.OrderPayTypeDto;
import com.ruoyi.system.domain.entity.OrderPayType;
import com.ruoyi.system.domain.vo.OrderPayTypeVo;


/**
 * 订单支付方式表(OrderPayType)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-23 21:16:07
 */
public interface IOrderPayTypeService {

    /**
     * 保存或更新虚拟订单号
     *
     * @param orderId         业务订单ID
     * @param payType         支付方式：alipay/wxpay
     * @param virtualOrderId  虚拟订单号
     */
    void saveVirtualOrderId(String orderId, String payType, String virtualOrderId);

    /**
     * 根据订单ID查询支付方式记录
     *
     * @param orderId 业务订单ID
     * @return OrderPayType
     */
    OrderPayType getByOrderId(String orderId);

}
