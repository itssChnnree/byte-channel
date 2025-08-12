package com.ruoyi.system.service.impl;


import com.ruoyi.system.constant.RedissonLockStatus;
import com.ruoyi.system.domain.dto.OrderByCommodityDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.api.annotation.RInject;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    private RedissonClient redissonClient;


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

        //订单入库

        //消息队列   余额扣减  邮箱发送

        //延迟队列   15分钟后提醒充值  30分钟后是否支付，未支付关闭订单

        return null;
    }


    private Order createOrder(OrderByCommodityDto orderByCommodityDto, Commodity normalCommodity) {
        Order order = new Order();
        order.setUserId(orderByCommodityDto.getUserId());
        order.setAmount(normalCommodity.getCommodityPrice()*orderByCommodityDto.getNum());
        order.setStatus("待支付");
        order.setOrderTime(new Date());
        return order;
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


}
