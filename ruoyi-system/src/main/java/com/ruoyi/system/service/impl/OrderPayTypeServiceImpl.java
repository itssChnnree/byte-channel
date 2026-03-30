package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.dto.OrderPayTypeDto;
import com.ruoyi.system.mapper.OrderPayTypeMapper;
import com.ruoyi.system.service.IOrderPayTypeService;
import com.ruoyi.system.domain.entity.OrderPayType;
import com.ruoyi.system.domain.vo.OrderPayTypeVo;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 订单支付方式表(OrderPayType)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-23 21:16:07
 */
@Service("orderPayTypeService")
public class OrderPayTypeServiceImpl implements IOrderPayTypeService {

    @Resource
    private OrderPayTypeMapper orderPayTypeMapper;

    @Override
    public void saveVirtualOrderId(String orderId, String payType, String virtualOrderId) {
        // 查询是否已存在记录
        OrderPayType orderPayType = getByOrderId(orderId);
        
        if (orderPayType == null) {
            // 新建记录
            orderPayType = new OrderPayType();
            orderPayType.setOrderId(orderId);
            orderPayType.setIsCheck(0);
            orderPayType.setRetryCount(0);
            orderPayType.setUserId(SecurityUtils.getStrUserId());
        } else {
            orderPayType.setUpdateTime(new Date());
        }
        
        // 根据支付方式设置虚拟订单号
        if ("wxpay".equals(payType)) {
            orderPayType.setWxOrderId(virtualOrderId);
        } else if ("alipay".equals(payType)) {
            orderPayType.setAlipayOrderId(virtualOrderId);
        }
        
        // 保存或更新
        if (orderPayType.getId() == null) {
            orderPayTypeMapper.insert(orderPayType);
            LogEsUtil.info("创建订单支付方式记录，订单ID：" + orderId + "，支付方式：" + payType + "，虚拟订单号：" + virtualOrderId);
        } else {
            orderPayTypeMapper.updateById(orderPayType);
            LogEsUtil.info("更新订单支付方式记录，订单ID：" + orderId + "，支付方式：" + payType + "，虚拟订单号：" + virtualOrderId);
        }
    }

    @Override
    public OrderPayType getByOrderId(String orderId) {
        return orderPayTypeMapper.selectByIdForUpdate(orderId);
    }

}
