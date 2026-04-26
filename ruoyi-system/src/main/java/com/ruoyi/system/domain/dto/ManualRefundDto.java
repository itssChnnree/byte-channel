package com.ruoyi.system.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 手动退款DTO
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/6
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ManualRefundDto {

    private String orderId;

    private BigDecimal refundAmount;

}
