package com.ruoyi.system.service.impl;


import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.Util.DefaultValueUtil;
import com.ruoyi.system.constant.RedissonLockStatus;
import com.ruoyi.system.constant.RocketMqConstant;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.dto.OrderByShoppingCartDto;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.domain.dto.OrderMessageDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import com.ruoyi.system.domain.vo.OrderVo;
import com.ruoyi.system.domain.vo.ShoppingCartCommodityVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.mapper.PromoCodeRecordsMapper;
import com.ruoyi.system.service.IOrderService;
import com.ruoyi.system.strategy.OrderCreate.OrderCreateContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 订单表(Order)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@Service("orderService")
@Slf4j
public class OrderServiceImpl implements IOrderService {


    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private RedissonClient redissonClient;

    @Resource
    private RocketMQTemplate rocketMqTemplate;

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;


    /**
     * [直接从商品创建订单]
     *
     * @param orderByCommodityDto 创建参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/10
     **/
    @Override
    @Transactional
    public Result createOrderByCommodity(OrderByCommodityDto orderByCommodityDto) {
        //校验商品是否存在
        Commodity normalCommodity = commodityMapper.findNormalCommodity(orderByCommodityDto.getCommodityId());
        if (normalCommodity == null) {
            return Result.fail("商品不存在");
        }
        try {
            DefaultValueUtil.setDefaultData(orderByCommodityDto);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        RLock lock = redissonClient.getLock(RedissonLockStatus.COMMODITY_STOCK_CHANGE_LOCK + orderByCommodityDto.getCommodityId());
        try{
            boolean lockStatus = lock.tryLock(5, TimeUnit.SECONDS);
            if (lockStatus){
                Boolean commodityStockStatus = checkCommodityStock(orderByCommodityDto, normalCommodity);
                if (!commodityStockStatus){
                    return Result.fail("商品库存不足");
                }
                commodityMapper.update(normalCommodity);
            }else {
                return Result.fail("系统繁忙，请稍后再试");
            }
        }catch (InterruptedException e){
            log.error("获取锁异常", e);
            throw new RuntimeException("系统繁忙，请稍后再试");
        }finally {
            lock.unlock();
        }
        //创建订单
        OrderCreateContext orderCreateContext = new OrderCreateContext(orderByCommodityDto.getPayCycle());
        //查询推广码
        PromoCodeRecords promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(orderByCommodityDto.getPromoCode());
        Order order = orderCreateContext.createOrder(orderByCommodityDto, normalCommodity, promoCodeRecords);
        //订单入库
        int insert = orderMapper.insert(order);
        if (insert<1){
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        //创建消息队列类
        OrderMessageDto orderMessageDto = new OrderMessageDto();
        orderMessageDto.setOrder(order);
        orderMessageDto.setOrderByCommodityDto(orderByCommodityDto);
        orderMessageDto.setPromoCodeRecordsDto(promoCodeRecords);

        //消息队列： 创建orderCommodity,取消时关闭订单并释放资源
        SendResult sendResult1 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_TOPIC, orderMessageDto);
        if (!sendResult1.getSendStatus().equals(SendStatus.SEND_OK)){
            SendResult sendResult2 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_TOPIC, orderMessageDto);
            if (!sendResult2.getSendStatus().equals(SendStatus.SEND_OK)){
                throw new RuntimeException("生成订单失败，请稍后再试");
            }
        }
        return Result.success(order);
    }



    //校验商品库存
    private Boolean checkCommodityStock(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity) {
        //判断库存是否满足，满足则直接扣减库存并返回
        if (normalCommodity.getInventory()>= orderByCommodityDto.getNum()){
            normalCommodity.setInventory(normalCommodity.getInventory()-orderByCommodityDto.getNum());
            return true;
        }else {
            //判断库存加未超卖数量是否满足购买数量
            if (normalCommodity.getInventory()+(normalCommodity.getOversoldConfigurations()- normalCommodity.getOversold())>= orderByCommodityDto.getNum()){
                //判断还需要扣减的超卖数
                int needOversold = orderByCommodityDto.getNum() - normalCommodity.getInventory();
                //将库存扣减为0，超卖数加上需要扣减的超卖数
                normalCommodity.setInventory(0);
                normalCommodity.setOversold(normalCommodity.getOversold()+needOversold);
                return true;
            }else {
                return false;
            }
        }
    }


    /**
     * [分页查询订单]
     *
     * @param orderDto 分页查询订单
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/14
     **/
    @Override
    public Result pageQuery(OrderDto orderDto) {
        Page<Order> page = new Page<>(orderDto.getPageNum(), orderDto.getPageSize());
        IPage<OrderVo> orderVoIPage = orderMapper.queryPage(page, orderDto, SecurityUtils.getStrUserId());
        return Result.success(orderVoIPage);
    }


    /**
     * [消息队列测试]
     *
     * @return java.lang.String
     * @author 陈湘岳 2025/8/14
     **/
    @Override
    public String test() {
//        for (int i = 0; i < 300; i++){
            OrderMessageDto orderMessageDto = new OrderMessageDto(20000, null, null, null);
            SendResult sendResult1 = rocketMqTemplate.syncSend(RocketMqConstant.ORDER_TOPIC, orderMessageDto);
//        }
        return "测试完毕";
    }
}
