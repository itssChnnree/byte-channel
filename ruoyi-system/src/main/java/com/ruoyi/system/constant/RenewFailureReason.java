package com.ruoyi.system.constant;

/**
 * [自动续费失败原因常量]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026/04/12
 */
public interface RenewFailureReason {

    /** 资源已下架 */
     String RESOURCE_OFF_SHELF = "资源已下架";

    /** 商品不存在 */
     String COMMODITY_NOT_FOUND = "商品不存在";


    /** 涨价幅度超过用户接受范围 */
     String PRICE_INCREASE_EXCEEDS_LIMIT = "涨价幅度超过用户接受范围";

    /** 未记录自动续费快照价格 */
    String AUTO_RENEWAL_SNAPSHOT_PRICE_NOT_RECORDED = "未记录自动续费快照价格";

    /** 余额不足 */
     String INSUFFICIENT_BALANCE = "余额不足";

    /** 订单创建失败 */
     String ORDER_CREATE_FAILED = "订单创建失败";

    /** 资源续费失败 */
     String RESOURCE_RENEWAL_FAILED = "资源续费失败";

    /** 自动续费异常 */
     String AUTO_RENEW_EXCEPTION = "自动续费异常";
}
