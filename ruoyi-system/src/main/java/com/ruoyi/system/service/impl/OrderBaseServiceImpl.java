package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.OrderPayType;
import com.ruoyi.system.domain.entity.ProfitFlow;
import com.ruoyi.system.domain.vo.YiPayResponse;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.pay.application.dto.request.QueryPayOrderRequest;
import com.ruoyi.system.pay.application.service.PayOrderApplicationService;
import com.ruoyi.system.pay.application.vo.QueryPayOrderVo;
import com.ruoyi.system.service.IOrderBaseService;
import com.ruoyi.system.service.IOrderPayTypeService;
import com.ruoyi.system.service.IProfitFlowService;
import com.ruoyi.system.util.LogEsUtil;

import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

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

    @Resource
    private RedisCache redisCache;

    @Resource
    private IOrderBaseService iOrderBaseService;

    @Resource
    private PayOrderApplicationService payOrderApplicationService;

    @Resource
    private IOrderPayTypeService orderPayTypeService;

    @Resource(name = "refundWhitelistCache")
    private Cache<String, String> refundWhitelistCache;

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

    @Override
    public Integer refoundToBalance(Boolean refoundToBalance) {
        String onlyRefundFlow = redisCache.getCacheObject("sys_config:sys:onlyRefound:flow");
        boolean onlyRefundToBalance = "true".equalsIgnoreCase(onlyRefundFlow);
        if (onlyRefundToBalance) {
            // 如果用户在退款白名单中，允许原路退款
            String username = SecurityUtils.getUsername();
            if (StrUtil.isNotBlank(username) && refundWhitelistCache.getIfPresent(username) != null) {
                return ObjectUtil.equals(refoundToBalance, Boolean.TRUE) ? 1 : 0;
            }
            return 1;
        }
        return ObjectUtil.equals(refoundToBalance, Boolean.TRUE) ? 1 : 0;
    }

    //调用订单平台获取订单信息
    public YiPayResponse getOrderInfo(Order order){
        try {
            // 查询订单支付方式记录
            OrderPayType orderPayType = orderPayTypeService.getByOrderId(order.getId());
            if (orderPayType == null) {
                LogEsUtil.info("订单未创建支付方式记录，订单id：" + order.getId());
                return null;
            }

            // 异步并行查询微信和支付宝的支付状态
            CompletableFuture<YiPayResponse> wxQueryFuture = CompletableFuture.supplyAsync(() -> {
                if (StrUtil.isBlank(orderPayType.getWxOrderId())) {
                    return null;
                }
                return querySingleOrderStatus(orderPayType.getWxOrderId(), OrderStatus.WECHAT_PAY);
            });

            CompletableFuture<YiPayResponse> aliQueryFuture = CompletableFuture.supplyAsync(() -> {
                if (StrUtil.isBlank(orderPayType.getAlipayOrderId())) {
                    return null;
                }
                return querySingleOrderStatus(orderPayType.getAlipayOrderId(), OrderStatus.ALIPAY_PAY);
            });

            // 等待所有查询完成，设置超时时间5秒
            CompletableFuture.allOf(wxQueryFuture, aliQueryFuture)
                    .orTimeout(5, TimeUnit.SECONDS)
                    .exceptionally(ex -> {
                        LogEsUtil.warn("查询支付状态超时，订单id：" + order.getId() + "，异常：" + ex.getMessage());
                        return null;
                    })
                    .join();

            // 获取查询结果
            YiPayResponse wxResult = wxQueryFuture.getNow(null);
            YiPayResponse aliResult = aliQueryFuture.getNow(null);

            // 任一支付成功即返回
            if (wxResult != null) {
                LogEsUtil.info("订单微信支付成功，订单id：" + order.getId());
                return wxResult;
            }
            if (aliResult != null) {
                LogEsUtil.info("订单支付宝支付成功，订单id：" + order.getId());
                return aliResult;
            }

            // 均未支付
            return null;
        } catch (Exception e) {
            LogEsUtil.error("查询订单支付状态异常，订单id：" + order.getId(), e);
            return null;
        }
    }

    /**
     * 查询单个虚拟订单的支付状态
     *
     * @param virtualOrderId 虚拟订单号
     * @param payType        支付方式
     * @return YiPayResponse 支付成功返回对象，未支付或查询失败返回null
     */
    private YiPayResponse querySingleOrderStatus(String virtualOrderId, String payType) {
        try {
            QueryPayOrderRequest queryRequest = new QueryPayOrderRequest();
            queryRequest.setOutTradeNo(virtualOrderId);
            Result<QueryPayOrderVo> queryResult = payOrderApplicationService.queryOrder(queryRequest);

            if (queryResult.getCode() != 200 || queryResult.getData() == null) {
                LogEsUtil.warn("查询订单支付状态失败，虚拟订单id：" + virtualOrderId);
                return null;
            }

            QueryPayOrderVo orderVo = queryResult.getData();
            if (orderVo.getStatus() == null || orderVo.getStatus() != 1) {
                // 订单未支付
                return null;
            }

            // 订单已支付，构造返回对象
            YiPayResponse yiPayResponse = new YiPayResponse();
            yiPayResponse.setPayId(orderVo.getTradeNo());
            yiPayResponse.setPayType(payType);

            return yiPayResponse;
        } catch (Exception e) {
            LogEsUtil.error("查询单个订单支付状态异常，虚拟订单id：" + virtualOrderId, e);
            return null;
        }
    }


    public Result<?> validStatus(String orderId, Order order) {
        //判断订单是否存在
        if (order == null|| order.getIsDeleted()!=0){
//            LogEsUtil.info("订单不存在："+orderId);
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
