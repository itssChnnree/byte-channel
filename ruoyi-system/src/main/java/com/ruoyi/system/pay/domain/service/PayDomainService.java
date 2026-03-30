package com.ruoyi.system.pay.domain.service;

import com.ruoyi.system.pay.domain.valueobject.PayStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * [支付领域服务]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/22
 */
@Slf4j
@Service
public class PayDomainService {

    /**
     * 校验金额是否合法
     */
    public boolean validateMoney(BigDecimal money) {
        if (money == null || money.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        // 最多2位小数
        return money.scale() <= 4;
    }

    /**
     * 校验商品名称长度（最大127字节）
     */
    public boolean validateName(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }
        // UTF-8编码下中文字符占3字节
        byte[] bytes = name.getBytes();
        return bytes.length <= 127;
    }

    /**
     * 校验订单号格式
     */
    public boolean validateOutTradeNo(String outTradeNo) {
        return outTradeNo != null && !outTradeNo.isEmpty() && outTradeNo.length() <= 32;
    }

    /**
     * 判断支付是否成功
     */
    public boolean isPaySuccess(String tradeStatus) {
        return "TRADE_SUCCESS".equals(tradeStatus);
    }

    /**
     * 获取支付状态描述
     */
    public String getPayStatusDesc(PayStatus status) {
        if (status == null) {
            return "未知状态";
        }
        return status.getDesc();
    }
}
