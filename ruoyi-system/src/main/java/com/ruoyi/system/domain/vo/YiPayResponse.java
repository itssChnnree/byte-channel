package com.ruoyi.system.domain.vo;

import com.ruoyi.system.constant.OrderStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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


    public String getPayType() {
        //todo 返回真实支付方式
        return Math.random()>0.5? OrderStatus.ALIPAY_PAY:OrderStatus.WECHAT_PAY;
    }
}
