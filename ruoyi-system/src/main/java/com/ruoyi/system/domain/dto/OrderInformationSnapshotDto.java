package com.ruoyi.system.domain.dto;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 订单快照(OrderInformationSnapshot)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-22 15:11:22
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderInformationSnapshotDto {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("订单id")
    private String orderId;

    @ApiModelProperty("购买用户id")
    private String userId;

    @ApiModelProperty("商品id")
    private String commodityId;

    @ApiModelProperty("商品名")
    private String commodityName;

    @ApiModelProperty("商品概述")
    private String commodityDesc;

    @ApiModelProperty("带宽")
    private Integer bandwidth;

    @ApiModelProperty("适合业务")
    private String businessSuitable;

    @ApiModelProperty("商品分类id")
    private String categoryId;

    @ApiModelProperty("分类名")
    private String categoryName;

    @ApiModelProperty("商品分类概述")
    private String categoryDesc;

    @ApiModelProperty("0为未删除，1为已删除")
    private Integer isDeleted;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("修改人")
    private String updateUser;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;


}
