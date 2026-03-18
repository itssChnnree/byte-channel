package com.ruoyi.system.service.impl;

import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.constant.OrderTypeConstant;
import com.ruoyi.system.domain.dto.ProfitFlowDto;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.ProfitFlow;
import com.ruoyi.system.domain.vo.YiPayResponse;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IOrderBaseService;
import com.ruoyi.system.service.IProfitFlowService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * [订单基座类]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/3/11
 */
@Service
public class OrderBaseServiceImpl implements IOrderBaseService {

    @Resource
    private IProfitFlowService profitFlowService;
    private OrderServiceImpl orderService;
    @Autowired
    private IOrderBaseService iOrderBaseService;

    //订单状态转化
    public String orderStatusConvert(String status) {
        switch (status) {
            case "WAIT_PAY":
                return "待支付";
            case "WAIT_ALLOCATION_RESOURCES":
                return "订单已支付，待分配资源";
            case "ALLOCATION_RESOURCES":
                return "资源分配中";
            case "COMPLETED":
                return "已完成";
            case "USER_CANCELED":
                return "用户主动取消";
            case "CANCELED_TIMEOUT":
                return "订单超时自动取消";
            case "WAIT_REFUND":
                return "订单待退款中";
            case "REFUND_SUCCESS":
                return "订单已退款";
            default:
                return "订单状态异常";
        }
    }

    //调用订单平台获取订单信息
    public YiPayResponse getOrderInfo(Order order){
        boolean b = Math.random() > 0.3;
        return b?new YiPayResponse():null;
    }


    public Result<?> validStatus(String orderId, Order order) {
        //判断订单是否存在
        if (order == null|| order.getIsDeleted()!=0){
            LogEsUtil.info("订单不存在："+orderId);
            return Result.fail("订单不存在");
        }
        String strUserId = SecurityUtils.getStrUserId();
        if (!strUserId.equals(order.getCreateUser())){
            LogEsUtil.info("用户无权访问此订单："+orderId);
            return Result.fail("您没有权限对此订单进行操作");
        }
        //如果订单不是待支付状态，代表不用确认付款
        if (!OrderStatus.WAIT_PAY.equals(order.getStatus())){
            LogEsUtil.info("订单状态不是待支付："+orderId);
            return Result.success(orderStatusConvert(order.getStatus()), false);
        }
        return null;
    }

    //添加利润流水
    public void addProfit(Order order,String desc) {
        LogEsUtil.info("添加利润流水："+order.getId()+",利润类型："+order.getOrderType()+"，利润金额："+order.getAmount());
        ProfitFlow profitFlow = new ProfitFlow();
        profitFlow.setProfitAmount(order.getAmount());
        profitFlow.setSourceType(order.getOrderType());
        profitFlow.setSourceId(order.getId());
        profitFlow.setSourceDesc(desc);
        profitFlowService.add(profitFlow);
    }


    //添加利润流水
    public void reduceProfit(Order order) {
        LogEsUtil.info("删除利润流水："+order.getId()+
                ",利润类型："+order.getOrderType()+"，利润金额："+order.getAmount());
        profitFlowService.deleteBySourceTypeAndId(order.getOrderType(), order.getId());
    }


}
