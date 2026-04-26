package com.ruoyi.system.domain.vo;

import com.ruoyi.system.constant.OrderStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * [易支付返回数据]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/1/15
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class YiPayResponse {

    @ApiModelProperty("支付类型")
    private String payType;

    @ApiModelProperty("支付平台id")
    private String payId;


    /** 支付宝支付方式集合（平台原始type码） */
    private static final Set<String> ALIPAY_TYPES = new HashSet<>(Arrays.asList("alipay"));

       /** 微信支付方式集合（平台原始type码） */
    private static final Set<String> WECHAT_TYPES = new HashSet<>(Arrays.asList("wxpay", "wechat"));

    public void setPayType(String payType) {
        if (payType == null) {
            this.payType = null;
        } else if (ALIPAY_TYPES.contains(payType)) {
            this.payType = OrderStatus.ALIPAY_PAY;
        } else if (WECHAT_TYPES.contains(payType)) {
            this.payType = OrderStatus.WECHAT_PAY;
        }else {
            this.payType = payType;
        }

    }
}
