package com.ruoyi.system.messageConsumer.orderCancel;

import com.ruoyi.system.constant.RedissonLockStatus;
import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.OrderCommodityResources;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import com.ruoyi.system.mapper.OrderCommodityResourcesMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.messageConsumer.OrderGeneral;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * [释放资源占用]
 *
 * @author
 * @version v1.0.0
 * @date 2025/8/19
 */
@RocketMQMessageListener(consumerGroup = RocketMqConstant.RELEASE_COMMODITY,
        topic = RocketMqConstant.ORDER_CANCEL_TOPIC)
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
            List<OrderCommodityResources> byOrderId = orderCommodityResourcesMapper.findByOrderId(orderMessageDto.getOrder().getId());
            List<String> collect = byOrderId.stream().map(OrderCommodityResources::getResourcesId).collect(Collectors.toList());
            int i = serverResourcesMapper.updateServerResourcesSaleStatus(collect);
        });
    }


}
