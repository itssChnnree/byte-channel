package com.ruoyi.system.service.impl;


import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.*;
import com.ruoyi.system.domain.dto.RechargeDto;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.domain.vo.OrderStatusTimelineVo;
import com.ruoyi.system.domain.vo.OrderVo;
import com.ruoyi.system.domain.vo.YiPayResponse;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.mapper.WalletBalanceDetailMapper;
import com.ruoyi.system.mapper.WalletBalanceMapper;
import com.ruoyi.system.mapstruct.OrderMapstruct;
import com.ruoyi.system.service.IOrderBaseService;
import com.ruoyi.system.service.IOrderStatusTimelineService;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 钱包余额表(WalletBalance)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:32
 */
@Service("walletBalanceService")
public class WalletBalanceServiceImpl implements IWalletBalanceService {


    @Resource
    private WalletBalanceMapper walletBalanceMapper;

    @Resource
    private WalletBalanceDetailMapper walletBalanceDetailMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private IOrderBaseService orderBaseService;

    @Resource
    private IOrderStatusTimelineService orderStatusTimelineService;

    @Resource
    OrderMapstruct orderMapstruct;

    /**
     * [查询余额]
     *
     * @return java.lang.String
     * @author 陈湘岳 2025/8/28
     **/
    @Override
    public Result getWalletBalance() {
        //查询用户余额
        String strUserId = SecurityUtils.getStrUserId();
        BigDecimal byUserId = walletBalanceMapper.findByUserId(strUserId);
        return Result.success(byUserId);
    }

    //余额扣减
    public Boolean reduceBalance(Order order){
        return reduceBalance(order.getAmount(),BalanceDetailStatus.BUY);
    }

    //余额扣减
    public Boolean reduceBalance(BigDecimal bigDecimal,String type){
        String userId = SecurityUtils.getStrUserId();
        //获取用户余额
        //一锁 余额
        WalletBalance walletBalanceByUserId = walletBalanceMapper.findWalletBalanceByUserId(userId);
        //二判 余额是否充足
        if (ObjectUtil.isNull(walletBalanceByUserId)) {
            LogEsUtil.warn("查询用户余额错误,用户id："+userId);
            throw new RuntimeException("查询用户余额错误，请联系管理员");
        }
        if (walletBalanceByUserId.getBalance().compareTo(bigDecimal) <= 0){
            LogEsUtil.info("用户余额不足,用户余额："+walletBalanceByUserId.getBalance()+",用户id："+userId);
            return false;
        }
        //余额充足，则扣减余额
        BigDecimal balance = walletBalanceByUserId.getBalance();
        walletBalanceByUserId.setBalance(balance.subtract(bigDecimal));
        int i = walletBalanceMapper.updateById(walletBalanceByUserId);
        if (i >= 1){
            //添加余额变更记录
            walletBalanceDetailMapper.insert(buildWalletBalanceDetail(walletBalanceByUserId, type, bigDecimal,"-"));
            LogEsUtil.info("用户余额变更成功,用户id："+userId+",扣减余额："+bigDecimal+",扣减后余额："+walletBalanceByUserId.getBalance());
            return true;
        }else {
            LogEsUtil.warn("扣减余额失败，用户id："+userId);
            throw new RuntimeException("余额变更失败，请联系管理员");
        }
    }

    //余额增加 - 从订单
    @Override
    public Boolean addBalance(Order order){
        return addBalance(order.getAmount(), BalanceDetailStatus.RECHARGE);
    }

    //余额增加 - 通用
    @Override
    public Boolean addBalance(BigDecimal amount, String type){
        String userId = SecurityUtils.getStrUserId();
        //一锁 余额
        WalletBalance walletBalanceByUserId = walletBalanceMapper.findWalletBalanceByUserId(userId);
        //二判 金额有效性
        if (ObjectUtil.isNull(walletBalanceByUserId)) {
            LogEsUtil.warn("查询用户余额错误,用户id："+userId);
            throw new RuntimeException("查询用户余额错误，请联系管理员");
        }
        if (BigDecimal.ZERO.compareTo(amount) >= 0){
            LogEsUtil.warn("增加余额金额必须大于0,金额："+amount+",用户id："+userId);
            throw new RuntimeException("增加余额金额必须大于0");
        }
        //增加余额
        BigDecimal balance = walletBalanceByUserId.getBalance();
        walletBalanceByUserId.setBalance(balance.add(amount));
        int i = walletBalanceMapper.updateById(walletBalanceByUserId);
        if (i >= 1){
            //添加余额变更记录
            walletBalanceDetailMapper.insert(buildWalletBalanceDetail(walletBalanceByUserId, type, amount,"+"));
            LogEsUtil.info("用户余额变更成功,用户id："+userId+",增加余额："+amount+",增加后余额："+walletBalanceByUserId.getBalance());
            return true;
        }else {
            LogEsUtil.warn("增加余额失败，用户id："+userId);
            throw new RuntimeException("余额变更失败，请联系管理员");
        }
    }


    private WalletBalanceDetail buildWalletBalanceDetail(WalletBalance walletBalance, Order order) {
        WalletBalanceDetail walletBalanceDetail = new WalletBalanceDetail();
        walletBalanceDetail.setUserId(walletBalance.getUserId());
        walletBalanceDetail.setChangeAmount(order.getAmount());
        walletBalanceDetail.setType(BalanceDetailStatus.BUY);
        walletBalanceDetail.setNowAmount(walletBalance.getBalance());
        return walletBalanceDetail;
    }

    private WalletBalanceDetail buildWalletBalanceDetail(WalletBalance walletBalance,
                                                         String type,
                                                         BigDecimal changeAmount,
                                                         String sign) {
        WalletBalanceDetail walletBalanceDetail = new WalletBalanceDetail();
        walletBalanceDetail.setUserId(walletBalance.getUserId());
        if ("+".equals( sign)){
            walletBalanceDetail.setChangeAmount(changeAmount);
        }else {
            walletBalanceDetail.setChangeAmount(changeAmount.negate());
        }
        walletBalanceDetail.setType(type);
        walletBalanceDetail.setNowAmount(walletBalance.getBalance());
        return walletBalanceDetail;
    }


    /**
     * [余额充值订单已支付]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/7
     **/
    @Override
    @Transactional
    public Result rechargeOrderIsPay(String orderId) {
        //获取订单状态
        //一锁
        Order order = orderMapper.queryByIdForUpdate(orderId);
        Result<?> validStatus = orderBaseService.validStatus(orderId, order);
        if (validStatus != null){
            return validStatus;
        }
        //如果第三方已支付，则走第三方支付,未支付则判断是否余额支付，是走余额支付，否则支付失败
        YiPayResponse yiPayResponse = orderBaseService.getOrderInfo(order);
        if (ObjectUtil.isNotNull(yiPayResponse)){
            order.setPaymentType(yiPayResponse.getPayType());
            order.setPaymentId(yiPayResponse.getPayId());
            orderBaseService.addProfit(order,"余额充值");
            LogEsUtil.info("订单已使用聚合支付，支付id["+orderId+"],支付方式为["+order.getPaymentType()+"]");
            addBalance( order.getAmount(), BalanceDetailStatus.RECHARGE);
            return Result.success(order);
        }else {
            return Result.fail("未检测到支付信息，请稍后再试");
        }

    }

    /**
     * [支付页-充值订单查询]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/7
     **/
    @Override
    public Result getRechargeOrderInfo(String orderId) {
        Order order = orderMapper.queryById(orderId);
        if (order == null){
            return Result.fail("订单不存在");
        }
        if (!SecurityUtils.getStrUserId().equals(order.getUserId())){
            return Result.fail("您没有权限查看此订单");
        }
        if (!OrderTypeConstant.RECHARGE.equals(order.getOrderType())){
            return Result.fail("订单类型不为充值");
        }
        OrderVo orderVo = orderMapstruct.change2Vo(order);
        return Result.success(orderVo);
    }

    /**
     * [取消充值订单]
     *
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/7
     **/
    @Override
    public Result cancelRechargeOrder(String orderId) {
        //一锁
        Order order = orderMapper.queryByIdForUpdate(orderId);
        //二判
        if (order == null){
            LogEsUtil.warn("订单不存在：" + orderId);
            return Result.fail("订单不存在");
        }
        if (!StrUtil.equals(OrderTypeConstant.RECHARGE,order.getOrderType())){
            LogEsUtil.warn("订单类型不为充值，订单id为：" + orderId);
            return Result.fail("该订单不是充值订单");
        }
        String strUserId = SecurityUtils.getStrUserId();
        if (!strUserId.equals(order.getCreateUser())){
            LogEsUtil.warn("用户对订单没有操作权限，订单id为：" + orderId);
            return Result.fail("对该订单暂无操作权限");
        }
        //已取消 超时系统取消
        if ( OrderStatus.CANCELED_TIMEOUT.equals(order.getStatus())){
            LogEsUtil.warn("订单超时自动取消，订单id为：" + orderId);
            return Result.fail("订单超时自动取消");
        }
        //已取消 用户主动取消
        if (OrderStatus.USER_CANCELED.equals(order.getStatus())){
            LogEsUtil.warn("用户已取消订单，订单id为：" + orderId);
            return Result.fail("用户已取消订单");
        }
        //待退款
        if (OrderStatus.WAIT_REFUND.equals(order.getStatus())){
            LogEsUtil.warn("订单正在退款中，订单id为：" + orderId);
            return Result.fail("订单正在退款中");
        }
        //已退款
        if (OrderStatus.REFUND_SUCCESS.equals(order.getStatus())){
            LogEsUtil.warn("订单已退款，订单id为：" + orderId);
            return Result.fail("订单已退款");
        }
        LogEsUtil.info("订单开始取消,订单id为：" + orderId);
        OrderStatusTimelineVo byOrderId = orderStatusTimelineService.getByOrderId(orderId);
        if(!ObjectUtils.isEmpty(byOrderId) && !ObjectUtils.isEmpty(byOrderId.getCompletedTime())){
            boolean timeBeforeHours = isTimeBeforeHours(byOrderId.getCompletedTime(), 7 * 24);
            if (!timeBeforeHours){
                LogEsUtil.warn("订单退款失败，订单已超过7天，订单id为：" + orderId);
                return Result.fail("订单退款失败，已超过退款时限");
            }
            //订单完成，需要扣减余额
            Boolean reduceBalance = reduceBalance(order.getAmount(), BalanceDetailStatus.RECHARGE_REFUND);
            if (!reduceBalance){
                LogEsUtil.warn("充值订单退款失败，余额不足，订单id为：" + orderId);
                return Result.fail("订单退款失败，余额不足");
            }
        }
        //订单时间线
        orderStatusTimelineService.setUserCanceledAndWaitRefundTime(orderId);
        String status = null;
        String result = null;
        if (OrderStatus.WAIT_PAY.equals(order.getStatus())){
            status = OrderStatus.USER_CANCELED;
            result = ResultMessage.CANCEL_ORDER_SUCCESS;
        }else {
            status = OrderStatus.WAIT_REFUND;
            result = ResultMessage.REFUND_ORDER_SUCCESS;
        }
        orderMapper.updateStatusById(orderId, status);
        LogEsUtil.info("订单取消成功,订单id为：{}",orderId);
        return Result.success(result);
    }

    /**
     * 传入一个时间，判断这个时间是否在多少小时之前,
     * 例如当时时间2026/1/18 14：40：00  传入时间 2026/1/17 13：40：00 hours 为24
     * 传入时间距今已超过24小时，返回false
     * @param targetDate 目标时间
     * @param hours 小时数
     * @return true: 目标时间在距当前hours小时之前
     */
    public static boolean isTimeBeforeHours(Date targetDate, int hours) {
        if (targetDate == null) {
            //订单未推进到已完成状态或极限情况，不存在时间线，直接退款
            return true;
        }

        Date now = new Date();
        long thresholdMillis = now.getTime() - (hours * 60 * 60 * 1000L);
        Date thresholdDate = new Date(thresholdMillis);

        LogEsUtil.info("订单可退款时间为:"+thresholdDate+" 之前,订单完结时间为:"+targetDate);
        return targetDate.getTime() > thresholdMillis;
    }


    /**
     * [创建充值订单]
     *
     * @param rechargeDto 床参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/7
     **/
    @Override
    public Result createOrderRecharge(RechargeDto rechargeDto) {

        if (ObjectUtil.isNull(rechargeDto.getRechargeAmount())){
            LogEsUtil.warn("用户创建充值订单失败：金额不能为空");
            return Result.fail("充值金额不能为空");
        }
        if (rechargeDto.getRechargeAmount().compareTo(BigDecimal.ZERO) <= 0){
            LogEsUtil.warn("用户创建充值订单失败：金额不能小于0");
            return Result.fail("充值金额不能小于0");
        }

            Order order = buildOrder(rechargeDto.getRechargeAmount());
            int insert = orderMapper.insert(order);
            if (insert<=0){
                LogEsUtil.warn("用户创建订单失败："+order);
                return Result.fail("创建订单失败");
            }
            orderStatusTimelineService.createOrderAndTimeline(order.getId());
            LogEsUtil.info("用户创建订单成功："+order);
            return Result.success(order);

    }

    private Order buildOrder(BigDecimal bigDecimal){
        Order order = new Order();
        order.setUserId(SecurityUtils.getStrUserId());
        order.setAmount(bigDecimal);
        order.setDescription("余额充值");
        order.setOrderType(OrderTypeConstant.RECHARGE);
        order.setPaymentPeriod(PaymentPeriodConstant.SINGLE);
        order.setStatus(OrderStatus.WAIT_PAY);
        order.setOrderTime(new Date());
        order.setCreateTime(new Date());
        order.setUpdateTime(new Date());
        order.setIsDeleted(0);
        return order;
    }
}
