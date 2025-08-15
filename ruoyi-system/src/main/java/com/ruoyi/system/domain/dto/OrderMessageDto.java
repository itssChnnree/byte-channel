package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [订单创建-消息队列]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/8/13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderMessageDto {

    private int i;

    private Order order;

    private OrderByCommodityDto orderByCommodityDto;

    private PromoCodeRecords promoCodeRecordsDto;
}
