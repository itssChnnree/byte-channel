package com.ruoyi.system.domain.entity;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


import java.io.Serializable;


/**
 * 服务器资源自动续费表(ServerResourcesRenewal)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-05 23:13:58
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "server_resources_renewal")
public class ServerResourcesRenewal extends ByteBaseEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 主键id
     */
    @TableId
    @TableField(value = "id")
    private String id;
    /**
     * 资源id
     */
    @TableField(value = "resources_id")
    private String resourcesId;
    /**
     * 续费开关,0为关，1为开
     */
    @TableField(value = "renewal_switch")
    private Integer renewalSwitch;
    /**
     * 开启用户
     */
    @TableField(value = "user_id")
    private String userId;
    /**
     * 续费周期
     */
    @TableField(value = "renewal_period")
    private String renewalPeriod;
    /**
     * 续费时间
     */
    @TableField(value = "renewal_time")
    private Date renewalTime;
    /**
     * 优惠卷
     */
    @TableField(value = "renewal_promo")
    private String renewalPromo;
    /**
     * 是否支持优惠码失效后原价续费
     */
    @TableField(value = "is_agree_original_price")
    private Integer isAgreeOriginalPrice;
    /**
     * 接受涨价的百分比
     */
    @TableField(value = "acceptable_price_increase_pct")
    private Integer acceptablePriceIncreasePct;
    /**
     * 价格快照
     */
    @TableField(value = "current_price_snapshot")
    private Double currentPriceSnapshot;

}
