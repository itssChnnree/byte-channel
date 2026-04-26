package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.helper.email.EmailSender;
import com.ruoyi.system.constant.*;
import com.ruoyi.system.domain.entity.*;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.service.IOrderStatusTimelineService;
import com.ruoyi.system.service.IResourceExpireNotifyService;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.strategy.OrderCreate.OrderCreateContext;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 资源到期通知服务实现类
 *
 * 核心设计：事务内先改状态再发邮件（防通知丢失）
 * 同一事务内：加锁 → 幂等校验 → 更新状态为NOTIFIED → 发送邮件 → 提交
 *
 * 异常处理策略：
 * - 邮件发送失败 → 异常传播 → 事务回滚 → 状态不变 → 下次任务重试（保证送达）
 * - 邮件发送成功 → 事务提交 → 状态变为NOTIFIED → 完成
 * - 极小概率风险：邮件已发出但事务提交失败 → 下次重复发送一封邮件（可接受，远优于通知丢失）
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/11
 */
@Service("resourceExpireNotifyService")
@Slf4j
public class ResourceExpireNotifyServiceImpl implements IResourceExpireNotifyService {

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private ServerResourcesRenewalMapper serverResourcesRenewalMapper;

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private UserConfigMapper userConfigMapper;

    @Resource
    private EmailSender emailSender;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderRenewalResourcesMapper orderRenewalResourcesMapper;

    @Resource
    private OrderInformationSnapshotMapper orderInformationSnapshotMapper;

    @Resource
    private PromoRecordsMapper promoRecordsMapper;

    @Resource
    private IWalletBalanceService iWalletBalanceService;

    @Resource
    private IOrderStatusTimelineService orderStatusTimelineService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Resource(name = "resourceDetectionExecutor")
    private Executor resourceDetectionExecutor;

    // 超时时间15秒
    private static final long TIMEOUT_SECONDS = 15;

    /**
     * 创建配置好隔离级别的TransactionTemplate
     * 传播行为: REQUIRES_NEW（独立事务）
     * 隔离级别: READ_COMMITTED
     */
    private TransactionTemplate createTransactionTemplate() {
        TransactionTemplate tt = new TransactionTemplate(transactionManager);
        tt.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        tt.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
        return tt;
    }

    @Override
    public int processExpireNotify() {
        // 查询3天内到期且状态为WAIT_NOTIFY的资源（限制100条）
        List<ServerResources> resourcesList = serverResourcesMapper.findResourcesExpireInThreeDays(100);

        if (resourcesList == null || resourcesList.isEmpty()) {
            LogEsUtil.info("没有需要处理的到期资源");
            return 0;
        }

        LogEsUtil.info("发现 " + resourcesList.size() + " 个即将到期的资源需要处理");
        AtomicInteger successCount = new AtomicInteger(0);

        // 并行处理每个资源（使用supplyAsync返回处理结果）
        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (ServerResources resource : resourcesList) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return processSingleResource(resource);
                } catch (Exception e) {
                    LogEsUtil.error("处理资源到期通知失败，资源id：" + resource.getId(), e);
                    return 0;
                }
            }, resourceDetectionExecutor);
            futures.add(future);
        }

        // 等待所有任务完成，带超时控制
        for (CompletableFuture<Integer> future : futures) {
            try {
                successCount.addAndGet(future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
            } catch (Exception e) {
                LogEsUtil.error("等待资源到期通知处理超时", e);
            }
        }

        LogEsUtil.info("资源到期通知处理完成，成功处理 " + successCount.get() + " 个资源");
        return successCount.get();
    }

    /**
     * 处理单个资源的到期通知
     *
     * 事务内设计：加锁 → 幂等校验 → 更新状态 → 发送邮件 → 提交
     * 
     * 保证：
     * - 邮件成功 + 事务提交 → 正常完成
     * - 邮件失败 → 异常传播 → 事务回滚 → 状态不变 → 下次重试（保证送达）
     * - 极小概率：邮件已发但事务提交失败 → 下次重复发送（可接受）
     */
    private int processSingleResource(ServerResources resource) {
        TransactionTemplate tt = createTransactionTemplate();

        Boolean result = tt.execute(status -> {
            try {
                // 1. 加锁查询资源（确保并发安全）
                ServerResources lockedResource = serverResourcesMapper.selectByIdForUpdate(resource.getId());
                if (lockedResource == null) {
                    LogEsUtil.warn("资源不存在，资源id：" + resource.getId());
                    return false;
                }

                // 2. 幂等校验：状态必须为WAIT_NOTIFY
                if (!ResourcesStatus.WAIT_NOTIFY.equals(lockedResource.getStatus())) {
                    LogEsUtil.info("资源状态已变更，无需处理，资源id：" + resource.getId());
                    return false;
                }

                // 2. 幂等校验：状态必须为WAIT_NOTIFY
                if (!SalesStatus.ON_SALE.equals(lockedResource.getSalesStatus())) {
                    LogEsUtil.info("资源未售出，无需处理，资源id：" + resource.getId());
                    return false;
                }

                // 3. 查询配置
                ServerResourcesRenewal renewalConfig = serverResourcesRenewalMapper
                        .findByUserIdAndResourcesId(lockedResource.getResourceTenant(), lockedResource.getId());
                UserConfig userConfig = userConfigMapper.selectByUserId(lockedResource.getResourceTenant());

                // 4. 判断动作类型并执行
                if (renewalConfig != null && renewalConfig.getRenewalSwitch() != null
                        && renewalConfig.getRenewalSwitch() == 1) {
                    // 开启自动续费
                    String renewResult = autoRenewResource(lockedResource, renewalConfig);
                    if (StrUtil.isBlank(renewResult)) {
                        LogEsUtil.info("自动续费成功，资源id：" + lockedResource.getId());
                    } else {
                        // 续费失败，判断是否开启续费失败邮件提醒
                        if (userConfig != null && userConfig.getRenewFailEmailNotice() != null
                                && userConfig.getRenewFailEmailNotice() == 1) {
                            sendRenewFailEmailInTransaction(lockedResource, renewResult);
                        }
                    }
                } else {
                    // 未开启自动续费，判断是否开启到期邮件提醒
                    if (userConfig != null && userConfig.getExpireEmailNotice() != null
                            && userConfig.getExpireEmailNotice() == 1) {
                        sendExpireNoticeEmailInTransaction(lockedResource);
                    }
                }

                // 5. 更新状态为NOTIFIED（必须在邮件发送成功后）
                int updated = serverResourcesMapper.updateNotifyStatusById(
                        lockedResource.getId(), ResourcesStatus.NOTIFIED);
                if (updated == 0) {
                    LogEsUtil.warn("更新通知状态失败，资源id：" + lockedResource.getId());
                    status.setRollbackOnly();
                    return false;
                }

                LogEsUtil.info("资源通知处理完成，资源id：" + lockedResource.getId());
                return true;

            } catch (Exception e) {
                LogEsUtil.error("处理单个资源到期通知异常，资源id：" + resource.getId(), e);
                status.setRollbackOnly();
                return false;
            }
        });

        return result != null && result ? 1 : 0;
    }

    /**
     * 自动续费资源（内部方法）
     *
     * 参考 createOrderByRenewal 和 renewalOrderIsPay 的实现
     * 
     * @param resource 资源i
     * @param renewalConfig 自动续费配置
     * @return String 失败原因(null表示成功)
     */
    private String autoRenewResource(ServerResources resource, ServerResourcesRenewal renewalConfig) {
        String resourcesId = resource.getId();
        try {
            // 2. 校验资源状态
            if (AvailableStatus.AVAILABLE_STATUS_DOWN.equals(resource.getAvailableStatus())) {
                LogEsUtil.warn("自动续费失败：资源已下架，资源id：" + resourcesId);
                return RenewFailureReason.RESOURCE_OFF_SHELF;
            }

            // 3. 查询商品
            Commodity commodity = commodityMapper.findByResourcesId(resourcesId);
            if (commodity == null) {
                LogEsUtil.warn("自动续费失败：商品不存在，资源id：" + resourcesId);
                return RenewFailureReason.COMMODITY_NOT_FOUND;
            }

            // 4. 查询推广码(从配置读取)
            PromoCodeRecords promoCode = null;
            if (StrUtil.isNotBlank(renewalConfig.getRenewalPromo())) {
                promoCode = promoCodeRecordsMapper.selectPromoCode(renewalConfig.getRenewalPromo());
                // 推广码失效时不立即失败，继续计算价格
            }

            // 5. 创建Order对象
            Order order = createRenewalOrder(renewalConfig, commodity, promoCode, resource);
            if (order == null) {
                LogEsUtil.warn("自动续费失败：订单创建异常，资源id：" + resourcesId);
                return RenewFailureReason.ORDER_CREATE_FAILED;
            }

            // 6. 使用订单价格校验涨价幅度
            String priceCheckResult = checkPriceIncrease(order.getAmount(), renewalConfig);
            if (priceCheckResult != null) {
                LogEsUtil.warn("自动续费失败：" + priceCheckResult + "，资源id：" + resourcesId);
                return priceCheckResult;
            }

            // 9. 插入订单记录
            int insert = orderMapper.insert(order);
            if (insert < 1) {
                LogEsUtil.warn("自动续费失败：订单插入失败，资源id：" + resourcesId);
                return RenewFailureReason.ORDER_CREATE_FAILED;
            }
            LogEsUtil.info("自动续费生成订单成功，订单id：" + order.getId() + "，资源id：" + resourcesId);

            // 10. 创建订单时间线
            orderStatusTimelineService.createOrderAndTimeline(order.getId());

            // 11. 创建推广记录
            insertPromoRecords(promoCode, order);

            // 12. 创建续费资源记录
            insertOrderRenewal(order, resource);

            // 13. 生成订单快照
            insertOrderInformationSnapshot(commodity.getId(), order.getId(), resource.getResourcesIp(),resource.getResourceTenant());
            order.setPaymentType(OrderStatus.BALANCE_PAY);
            // 8. 扣减余额
            Boolean balanceResult = iWalletBalanceService.reduceBalance(order);
            if (!balanceResult) {
                LogEsUtil.warn("自动续费失败：余额不足，资源id：" + resourcesId);
                return RenewFailureReason.INSUFFICIENT_BALANCE;
            }
            OrderServiceImpl bean = SpringUtil.getBean(OrderServiceImpl.class);
            Result<Boolean> booleanResult = bean.renewalResources(order.getId(), order);
            if (booleanResult.getData()) {
                LogEsUtil.info("自动续费成功，资源id：" + resourcesId + "，订单id：" + order.getId());
                
                // 异步发送续费成功邮件
                sendRenewSuccessEmailAsync(resource, commodity, order);
                
                // 成功返回null
                return null;
            }else {
                return RenewFailureReason.RESOURCE_RENEWAL_FAILED;
            }
        } catch (Exception e) {
            LogEsUtil.error("自动续费异常，资源id：" + resourcesId + "，异常信息：" + e.getMessage(), e);
            return RenewFailureReason.AUTO_RENEW_EXCEPTION + "：" + e.getMessage();
        }
    }

    /**
     * 计算当前最终价格
     */
    private BigDecimal calculateCurrentPrice(Commodity commodity, PromoCodeRecords promoCode, String paymentPeriod) {
        OrderCreateContext orderCreateContext = new OrderCreateContext(paymentPeriod);
        // 直接调用策略层的calculatePrice方法
        BigDecimal price = getCommodityPrice(commodity);
        return orderCreateContext.getOrderCreateStrategy().calculatePrice(1, promoCode != null, price);
    }

    /**
     * 获取商品价格(优先使用折扣价)
     */
    private BigDecimal getCommodityPrice(Commodity commodity) {
        if (ObjectUtil.isNotEmpty(commodity.getCommodityDiscountedPrice())) {
            return commodity.getCommodityDiscountedPrice();
        }
        return commodity.getCommodityPrice();
    }

    /**
     * 校验涨价幅度
     */
    private String checkPriceIncrease(BigDecimal currentPrice, ServerResourcesRenewal renewalConfig) {
        BigDecimal snapshotPrice = renewalConfig.getCurrentPriceSnapshot();
        if (snapshotPrice == null || snapshotPrice.compareTo(BigDecimal.ZERO) <= 0) {
            // 无快照价格，跳过校验
            return RenewFailureReason.AUTO_RENEWAL_SNAPSHOT_PRICE_NOT_RECORDED;
        }

        BigDecimal increase = currentPrice.subtract(snapshotPrice);
        if (increase.compareTo(BigDecimal.ZERO) <= 0) {
            // 未涨价
            return null;
        }

        Integer acceptablePct = renewalConfig.getAcceptablePriceIncreasePct();
        if (acceptablePct == null) {
            // 默认不接受涨价
            acceptablePct = 0;
        }

        // 计算涨幅比例: (当前价格 - 快照价格) / 快照价格 * 100
        BigDecimal increaseRatio = increase.divide(snapshotPrice, 4, RoundingMode.HALF_UP)
                                          .multiply(new BigDecimal("100"));

        if (increaseRatio.compareTo(new BigDecimal(acceptablePct)) > 0) {
            LogEsUtil.warn("涨价幅度超过限制：当前涨幅" + increaseRatio + "%，用户接受" + acceptablePct + "%");
            return RenewFailureReason.PRICE_INCREASE_EXCEEDS_LIMIT;
        }

        return null;
    }

    /**
     * 创建续费订单对象
     */
    private Order createRenewalOrder(ServerResourcesRenewal renewalConfig, Commodity commodity,
                                     PromoCodeRecords promoCode, ServerResources resource) {
        try {
            OrderCreateContext orderCreateContext = new OrderCreateContext(renewalConfig.getRenewalPeriod());
            Order order = orderCreateContext.createRenewalOrder(commodity, promoCode);

            // 设置用户ID(使用资源的租户ID)
            order.setUserId(resource.getResourceTenant());

            return order;
        } catch (Exception e) {
            LogEsUtil.error("创建续费订单对象异常", e);
            return null;
        }
    }

    /**
     * 创建推广记录
     */
    private void insertPromoRecords(PromoCodeRecords promoCodeRecords, Order order) {
        if (promoCodeRecords == null) {
            return;
        }
        PromoRecords promoRecords = new PromoRecords();
        promoRecords.setUserId(promoCodeRecords.getUserId());
        promoRecords.setReferralsUserId(order.getUserId());
        promoRecords.setPromoCodeRecordsId(promoCodeRecords.getId());
        promoRecords.setOrderId(order.getId());
        BigDecimal amount = order.getAmount();
        promoRecords.setCashbackAmount(amount.multiply(new BigDecimal("0.1")));
        promoRecords.setPurchaseAmount(amount);
        promoRecords.setStatus(OrderStatus.WAIT_CONFIRM);
        promoRecords.setCashbackPercentage("10%");
        int insert = promoRecordsMapper.insert(promoRecords);
        if (insert < 1) {
            LogEsUtil.warn("订单推广信息生成失败：" + promoRecords);
            throw new RuntimeException("生成订单失败，请稍后再试");
        }
        LogEsUtil.info("订单推广信息：" + promoRecords);
    }

    /**
     * 创建续费资源记录
     */
    private void insertOrderRenewal(Order order, ServerResources serverResources) {
        OrderRenewalResources orderRenewalResources = OrderRenewalResources.builder()
                .orderId(order.getId())
                .resourcesId(serverResources.getId())
                .resourcesIp(serverResources.getResourcesIp())
                .userId(order.getUserId())
                .build();
        int insert = orderRenewalResourcesMapper.insert(orderRenewalResources);
        LogEsUtil.info("订单续费资源信息：" + orderRenewalResources);
    }

    /**
     * 生成订单快照
     */
    private void insertOrderInformationSnapshot(String commodityId, String orderId, String ip,String userId) {
        OrderInformationSnapshot orderInformationSnapshot = commodityMapper.selectSnapshotByCommodityId(commodityId);
        if (orderInformationSnapshot == null) {
            LogEsUtil.warn("查询订单资源快照失败");
            throw new RuntimeException("生成订单资源快照失败");
        }
        orderInformationSnapshot.setOrderId(orderId);
        orderInformationSnapshot.setUserId(userId);
        if (StrUtil.isNotBlank(ip)) {
            orderInformationSnapshot.setIp(ip);
        }
        int insert = orderInformationSnapshotMapper.insert(orderInformationSnapshot);
        if (insert < 1) {
            LogEsUtil.info("生成订单快照资源失败");
            throw new RuntimeException("生成订单资源快照失败");
        }
        LogEsUtil.info("生成订单快照资源成功：" + orderInformationSnapshot);
    }

    /**
     * 续费资源(参考renewalOrderIsPay的实现)
     */
    private String renewalResources(Order order, ServerResources serverResources) {
        if (ObjectUtil.isNull(serverResources)) {
            LogEsUtil.warn("未查询到续费资源,订单id:" + order.getId());
            return null;
        }

        // 判断是否到期,到期则在当前时间上续费，未到期则在到期时间上续费
        Date nowDate;
        if (ObjectUtil.isNull(serverResources.getLeaseExpirationTime())
                || serverResources.getLeaseExpirationTime().getTime() < System.currentTimeMillis()) {
            nowDate = new Date();
        } else {
            nowDate = serverResources.getLeaseExpirationTime();
        }

        Date timeByPaymentPeriod = getTimeByPaymentPeriod(order.getPaymentPeriod(), nowDate);
        // 更新到期时间
        serverResources.setLeaseExpirationTime(timeByPaymentPeriod);
        serverResources.setResourceTenant(order.getUserId());
        serverResources.setSalesStatus(SalesStatus.ON_SALE);
        serverResources.setStatus(ResourcesStatus.WAIT_NOTIFY);
        int i = serverResourcesMapper.updateById(serverResources);
        if (i < 1) {
            LogEsUtil.warn("资源续费失败,订单id:" + order.getId() + ",资源id：" + serverResources.getId());
            return null;
        } else {
            return serverResources.getId();
        }
    }

    /**
     * 通过付款周期获取到期时间
     */
    private Date getTimeByPaymentPeriod(String paymentPeriod, Date date) {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.setTime(date);

        switch (paymentPeriod) {
            case PaymentPeriodConstant.MONTHLY:
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 30);
                break;
            case PaymentPeriodConstant.QUARTERLY:
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 90);
                break;
            case PaymentPeriodConstant.YEARLY:
                calendar.add(java.util.Calendar.DAY_OF_YEAR, 365);
                break;
        }
        return calendar.getTime();
    }

    /**
     * 发送到期提醒邮件（事务内调用，throws Exception触发回滚）
     */
    private void sendExpireNoticeEmailInTransaction(ServerResources resource) throws Exception {
        String userEmail = getUserEmail(resource.getResourceTenant());
        if (userEmail == null || userEmail.isEmpty()) {
            LogEsUtil.warn("用户邮箱为空，无法发送到期提醒，用户id：" + resource.getResourceTenant());
            return;
        }

        emailSender.sendResourceExpireNoticeEmail(
                userEmail,
                resource.getResourceTenant(),
                resource.getResourcesIp(),
                resource.getLeaseExpirationTime()
        );
        LogEsUtil.info("到期提醒邮件发送成功，资源id：" + resource.getId());
    }

    /**
     * 发送续费失败邮件（事务内调用，throws Exception触发回滚）
     */
    private void sendRenewFailEmailInTransaction(ServerResources resource, String failReason) throws Exception {
        String userEmail = getUserEmail(resource.getResourceTenant());
        if (userEmail == null || userEmail.isEmpty()) {
            LogEsUtil.warn("用户邮箱为空，无法发送续费失败提醒，用户id：" + resource.getResourceTenant());
            return;
        }

        emailSender.sendResourceRenewFailEmail(
                userEmail,
                resource.getResourceTenant(),
                resource.getResourcesIp(),
                failReason
        );
        LogEsUtil.info("续费失败邮件发送成功，资源id：" + resource.getId());
    }

    /**
     * 获取用户邮箱
     * SysUserMapper.selectUserById 接受 Long 类型，resourceTenant 为 String，需转换
     */
    private String getUserEmail(String userId) {
        try {
            SysUser user = sysUserMapper.selectUserById(Long.parseLong(userId));
            return user != null ? user.getEmail() : null;
        } catch (NumberFormatException e) {
            LogEsUtil.warn("用户id格式异常，无法查询邮箱，用户id：" + userId);
            return null;
        }
    }

    /**
     * 异步发送续费成功邮件
     */
    private void sendRenewSuccessEmailAsync(ServerResources resource, Commodity commodity, Order order) {
        ServerResources serverResources = serverResourcesMapper.findByIdForUpdate(resource.getId());
        CompletableFuture.runAsync(() -> {
            try {
                String userEmail = getUserEmail(resource.getResourceTenant());
                if (userEmail == null || userEmail.isEmpty()) {
                    LogEsUtil.warn("用户邮箱为空，无法发送续费成功通知，用户id：" + resource.getResourceTenant());
                    return;
                }

                emailSender.sendResourceRenewSuccessEmail(
                        userEmail,
                        resource.getResourceTenant(),
                        resource.getResourcesIp(),
                        commodity.getCommodityName(),
                        order.getAmount().toString(),
                        serverResources.getLeaseExpirationTime()
                );
                LogEsUtil.info("续费成功邮件发送成功，资源id：" + resource.getId() + "，订单id：" + order.getId());
            } catch (Exception e) {
                LogEsUtil.error("发送续费成功邮件失败，资源id：" + resource.getId(), e);
                // 邮件发送失败不影响主流程，仅记录日志
            }
        }, resourceDetectionExecutor);
    }
}
