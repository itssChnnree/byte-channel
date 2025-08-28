package com.ruoyi.system.messageConsumer.OrderAdd.orderAddCancel;

import com.ruoyi.system.constant.OrderCommodityResourcesStatus;
import com.ruoyi.system.constant.OrderTypeConstant;
import com.ruoyi.system.constant.PaymentPeriodConstant;
import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.OrderCommodityResources;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import com.ruoyi.system.mapper.OrderCommodityResourcesMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * [释放资源占用]
 *
 * @author
 * @version v1.0.0
 * @date 2025/8/19
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.RELEASE_COMMODITY,
        topic = RocketMqConstant.ORDER_ADD_CANCEL_TOPIC)
@Slf4j
@Service
public class ReleaseResourcesConsumer implements RocketMQListener<OrderMessageDto> {


    @Resource
    private OrderGeneral orderGeneral;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private OrderCommodityMapper orderCommodityMapper;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private OrderCommodityResourcesMapper orderCommodityResourcesMapper;

    @Resource
    private RedissonClient redissonClient;

    @Override
    public void onMessage(OrderMessageDto orderMessageDto) {
        orderGeneral.orderGeneral(orderMessageDto, RocketMqConstant.RELEASE_COMMODITY, () -> {
            if (OrderTypeConstant.ADD.equals(orderMessageDto.getOrder().getOrderType())){
                //如果是新增则释放新增的资源
                releaseAddResources(orderMessageDto);
            }else if (OrderTypeConstant.RENEW.equals(orderMessageDto.getOrder().getOrderType())){
                //如果是续费则将资源减去续费日期
                releaseRenewResources(orderMessageDto);
            }else {
                //若为充值不做处理
                return;
            }

        });
    }

    private void releaseAddResources(OrderMessageDto orderMessageDto) {
        List<OrderCommodityResources> byOrderId = orderCommodityResourcesMapper.findByOrderId(orderMessageDto.getOrder().getId());
        List<String> collect = byOrderId.stream().map(OrderCommodityResources::getResourcesId).collect(Collectors.toList());
        int i = serverResourcesMapper.updateServerResourcesSaleStatus(collect);
    }

    private void releaseRenewResources(OrderMessageDto orderMessageDto) {
        //查询已完成续费的资源
        List<OrderCommodityResources> byOrderId
                = orderCommodityResourcesMapper.findByOrderIdAndStatus(
                        orderMessageDto.getOrder().getId(), OrderCommodityResourcesStatus.SUCCESS);
        List<String> collect = byOrderId.stream().map(OrderCommodityResources::getResourcesId).collect(Collectors.toList());
        String paymentPeriod = orderMessageDto.getOrder().getPaymentPeriod();
        //按月缴费则在当前时间倒退一个月，其余同理
        if (PaymentPeriodConstant.MONTHLY.equals(paymentPeriod)){
            int i = serverResourcesMapper.updateLeaseExpirationTimeMonth(collect);
        } else if (PaymentPeriodConstant.QUARTERLY.equals(paymentPeriod)) {
            int i = serverResourcesMapper.updateLeaseExpirationTimeQuarter(collect);
        }else if(PaymentPeriodConstant.YEARLY.equals(paymentPeriod)){
            int i = serverResourcesMapper.updateLeaseExpirationTimeYear(collect);
        }
    }


}
