package com.ruoyi.system.service.impl;

import com.ruoyi.system.constant.BalanceDetailStatus;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.domain.entity.PromoRecords;
import com.ruoyi.system.mapper.PromoRecordsMapper;
import com.ruoyi.system.service.IPromoCashbackService;
import com.ruoyi.system.service.IWalletBalanceService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * 推广返现服务实现
 * 遵循项目规范：编程式事务(REQUIRES_NEW + READ_COMMITTED) + CompletableFuture异步 + FOR UPDATE行锁
 *
 * @author 陈湘岳
 * @date 2026/4/7
 */
@Service
public class PromoCashbackServiceImpl implements IPromoCashbackService {

    @Resource
    private PromoRecordsMapper promoRecordsMapper;

    @Resource
    private IWalletBalanceService walletBalanceService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Resource(name = "resourceDetectionExecutor")
    private Executor resourceDetectionExecutor;

    /** 超时时间15秒 */
    private static final long TIMEOUT_SECONDS = 15;

    /** 批次大小限制 */
    private static final int BATCH_SIZE = 100;

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
    public int execute() {
        List<PromoRecords> waitCashbackRecords = promoRecordsMapper.selectWaitCashbackRecords(BATCH_SIZE);
        if (CollectionUtils.isEmpty(waitCashbackRecords)) {
            LogEsUtil.info("没有需要返现的推广记录");
            return 0;
        }
        LogEsUtil.info("查找到 " + waitCashbackRecords.size() + " 条推广记录需要返现");

        List<CompletableFuture<Integer>> futures = new ArrayList<>();

        for (PromoRecords record : waitCashbackRecords) {
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return processSingleCashback(record);
                } catch (Exception e) {
                    LogEsUtil.error("处理推广返现失败，记录id：" + record.getId(), e);
                    return 0;
                }
            }, resourceDetectionExecutor);
            futures.add(future);
        }

        // 等待所有任务完成，带超时控制
        int successCount = 0;
        for (CompletableFuture<Integer> future : futures) {
            try {
                successCount += future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (Exception e) {
                LogEsUtil.error("等待推广返现处理超时", e);
            }
        }

        LogEsUtil.info("推广记录返现完毕，成功：" + successCount + " / " + waitCashbackRecords.size());
        return successCount;
    }

    /**
     * 处理单条推广记录返现
     *
     * 事务内设计：加锁 → 幂等校验 → 数据校验 → 增加余额 → 更新状态 → 提交
     */
    private int processSingleCashback(PromoRecords record) {
        String recordId = record.getId();
        TransactionTemplate tt = createTransactionTemplate();

        try {
            Integer result = tt.execute(status -> {
                // 1. FOR UPDATE lock the record
                PromoRecords lockedRecord = promoRecordsMapper.selectByIdForUpdate(recordId);
                if (lockedRecord == null || !OrderStatus.WAIT_CONFIRM.equals(lockedRecord.getStatus())) {
                    LogEsUtil.info("推广记录已处理或不存在，记录id：" + recordId);
                    return 0;
                }

                // 2. 使用加锁后的数据
                String userId = lockedRecord.getUserId();
                BigDecimal cashbackAmount = lockedRecord.getCashbackAmount();

                // 3. 校验返现金额
                if (cashbackAmount == null || BigDecimal.ZERO.compareTo(cashbackAmount) >= 0) {
                    LogEsUtil.error("返现金额无效，记录id：" + recordId + "，金额：" + cashbackAmount, new RuntimeException("Invalid cashback amount"));
                    return 0;
                }

                // 4. 增加推荐人余额（addBalance失败时抛异常，会触发事务回滚）
                Boolean added = walletBalanceService.addBalance(cashbackAmount, BalanceDetailStatus.CASHBACK, userId);
                if (!Boolean.TRUE.equals(added)) {
                    throw new RuntimeException("增加返现余额失败，记录id：" + recordId + "，用户id：" + userId);
                }

                // 5. 更新状态为已返现
                lockedRecord.setStatus(OrderStatus.RETURN_CASH);
                int updated = promoRecordsMapper.updateById(lockedRecord);
                if (updated <= 0) {
                    throw new RuntimeException("更新推广记录状态失败，记录id：" + recordId);
                }

                LogEsUtil.info("推广返现成功，记录id：" + recordId + "，用户id：" + userId + "，金额：" + cashbackAmount);
                return 1;
            });
            return result != null ? result : 0;
        } catch (Exception e) {
            LogEsUtil.error("处理推广返现失败，记录id：" + recordId, e);
            return 0;
        }
    }
}
