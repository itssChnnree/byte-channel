package com.ruoyi.system.domain.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/10/24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPayUrlVo {

    //扫码支付二维码连接
    private String payUrl;

    //支付方式
    private String payType;

    //订单号
    private String orderId;

    //支付金额
    private BigDecimal payMoney;

}
