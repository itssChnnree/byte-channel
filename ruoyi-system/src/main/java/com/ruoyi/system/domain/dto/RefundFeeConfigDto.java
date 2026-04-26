package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


/**
 * 退款费率及开关配置DTO
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RefundFeeConfigDto {

    /** 微信手续费比例，不传则不修改 */
    private BigDecimal wechatFeeRate;

    /** 支付宝手续费比例，不传则不修改 */
    private BigDecimal alipayFeeRate;

    /** 仅退款到余额开关，"true"/"false"，不传则不修改 */
    private String onlyRefundToBalance;

}
