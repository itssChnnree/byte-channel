package com.ruoyi.system.service.impl;

import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.constant.SalesStatus;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.entity.OrderCommodity;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.mapper.OrderCommodityMapper;
import com.ruoyi.system.mapper.OrderMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.service.IOrderStatusTimelineService;
import com.ruoyi.system.service.IResourceAllocationService;
import com.ruoyi.system.service.IServerResourcesService;
import com.ruoyi.system.util.LogEsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Resource Allocation Service Implementation
 *
 * @author ruoyi
 * @date 2026/3/23
 */
@Slf4j
@Service
public class ResourceAllocationServiceImpl implements IResourceAllocationService {

    @Resource
    private OrderMapper orderMapper;

    @Resource
    private OrderCommodityMapper orderCommodityMapper;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private IOrderStatusTimelineService orderStatusTimelineService;

    @Resource
    private PlatformTransactionManager transactionManager;

    @Resource
    private IServerResourcesService serverResourcesService;

    // Thread pool configuration
    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 10;
    private static final int QUEUE_CAPACITY = 100;
    private static final long KEEP_ALIVE_TIME = 60L;

    @Override
    public int execute() {
        LogEsUtil.info("Resource allocation task started");

        // 1. Query orders waiting for resource allocation
        List<Order> orders = orderMapper.selectWaitAllocationOrders();
        if (orders == null || orders.isEmpty()) {
            LogEsUtil.info("No orders waiting for resource allocation");
            return 0;
        }

        LogEsUtil.info("Found " + orders.size() + " orders waiting for resource allocation");

        // 2. Create thread pool
        ThreadPoolExecutor executor = createThreadPoolExecutor();

        // 3. Submit tasks
        List<Future<Integer>> futures = new CopyOnWriteArrayList<>();

        for (Order order : orders) {
            Future<Integer> future = executor.submit(() -> processOrder(order));
            futures.add(future);
        }

        // 4. Wait for all tasks to complete
        int successCount = 0;
        for (Future<Integer> future : futures) {
            try {
                Integer result = future.get(30, TimeUnit.SECONDS);
                if (result != null && result > 0) {
                    successCount++;
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                LogEsUtil.error("Resource allocation task interrupted", e);
            } catch (ExecutionException e) {
                LogEsUtil.error("Resource allocation task execution exception", e);
            } catch (TimeoutException e) {
                LogEsUtil.error("Resource allocation task timeout", e);
            }
        }

        // 5. Shutdown thread pool
        executor.shutdown();
        try {
            if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            executor.shutdownNow();
            Thread.currentThread().interrupt();
        }

        LogEsUtil.info("Resource allocation task completed, successfully allocated " + successCount + " orders");
        return successCount;
    }

    /**
     * Create thread pool
     */
    private ThreadPoolExecutor createThreadPoolExecutor() {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                MAX_POOL_SIZE,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(QUEUE_CAPACITY),
                new ThreadFactory() {
                    private final AtomicInteger counter = new AtomicInteger(0);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r);
                        t.setName("resource-allocation-" + counter.incrementAndGet());
                        t.setDaemon(true);
                        return t;
                    }
                },
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

    /**
     * Process single order resource allocation (executed in independent thread)
     *
     * @param order Order
     * @return 1-success, 0-failure
     */
    private Integer processOrder(Order order) {
        String orderId = order.getId();

        // Create transaction template
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
        transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);

        try {
            return transactionTemplate.execute(status -> {
                try {
                    orderMapper.queryByIdForUpdate(orderId);
                    // 1. Query order commodity
                    OrderCommodity orderCommodity = orderCommodityMapper.findByOrderId(orderId);
                    if (orderCommodity == null) {
                        LogEsUtil.warn("Order " + orderId + " commodity info not found");
                        return 0;
                    }

                    // 2. Query available resource (with row lock)
                    ServerResources resource = serverResourcesService.findByCommodityId(orderCommodity.getCommodityId());
                    if (resource == null) {
                        LogEsUtil.warn("Order " + orderId + " no available resource, commodityId: " + orderCommodity.getCommodityId());
                        return 0;
                    }

                    // 3. Allocate resource
                    boolean allocated = allocateResource(order, resource);
                    if (!allocated) {
                        LogEsUtil.error("Order " + orderId + " resource allocation failed", new RuntimeException("Allocation failed"));
                        status.setRollbackOnly();
                        return 0;
                    }

                    // 4. Update order status to COMPLETED
                    int updated = orderMapper.updateStatusById(orderId, OrderStatus.COMPLETED);
                    if (updated <= 0) {
                        LogEsUtil.error("Order " + orderId + " status update failed", new RuntimeException("Status update failed"));
                        status.setRollbackOnly();
                        return 0;
                    }

                    // 5. Record order status timeline
                    orderStatusTimelineService.setCompletedTime(orderId);

                    LogEsUtil.info("Order " + orderId + " resource allocated successfully, resourceId: " + resource.getId());
                    return 1;

                } catch (Exception e) {
                    LogEsUtil.error("Order " + orderId + " resource allocation exception: " + e.getMessage(), e);
                    status.setRollbackOnly();
                    return 0;
                }
            });
        } catch (Exception e) {
            LogEsUtil.error("Order " + orderId + " transaction execution exception: " + e.getMessage(), e);
            return 0;
        }
    }

    /**
     * Allocate resource to order
     *
     * @param order    Order
     * @param resource Resource
     * @return success or not
     */
    private boolean allocateResource(Order order, ServerResources resource) {
        try {
            // Calculate expiration time
            Date expirationTime = calculateExpirationTime(order.getPaymentPeriod());

            // Update resource info
            resource.setResourceTenant(order.getUserId());
            resource.setLeaseExpirationTime(expirationTime);
            resource.setSalesStatus(SalesStatus.ON_SALE);

            int updated = serverResourcesMapper.updateById(resource);
            return updated > 0;
        } catch (Exception e) {
            LogEsUtil.error("Allocate resource failed, orderId: " + order.getId() + ", resourceId: " + resource.getId(), e);
            return false;
        }
    }

    /**
     * Calculate expiration time based on payment period
     *
     * @param paymentPeriod Payment period
     * @return Expiration time
     */
    private Date calculateExpirationTime(String paymentPeriod) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        if (paymentPeriod == null) {
            calendar.add(Calendar.DAY_OF_YEAR, 30);
            return calendar.getTime();
        }

        switch (paymentPeriod) {
            case "月付":
                calendar.add(Calendar.DAY_OF_YEAR, 30);
                break;
            case "季付":
                calendar.add(Calendar.DAY_OF_YEAR, 90);
                break;
            case "年付":
                calendar.add(Calendar.DAY_OF_YEAR, 365);
                break;
            default:
                calendar.add(Calendar.DAY_OF_YEAR, 30);
        }

        return calendar.getTime();
    }
}
