package com.ruoyi.system.domain.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [资源分页查询]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/9/17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesPageVo {


    @ApiModelProperty("资源id")
    private String resourcesId;

    @ApiModelProperty("资源ip")
    private String resourcesIp;

    @ApiModelProperty("商品id")
    private String commodityId;

    @ApiModelProperty("商品名")
    private String commodityName;

    @ApiModelProperty("商品分类id")
    private String commodityCategoryId;

    @ApiModelProperty("商品分类")
    private String commodityCategoryName;

    @ApiModelProperty("云服务商名")
    private String vendorName;

    @ApiModelProperty("云服务商账号id")
    private String vendorAccountId;

    @ApiModelProperty("售卖状态")
    private String salesStatus;

    @ApiModelProperty("可售状态，可售状态,0为下架，1为上架")
    private Integer availableStatus;

    @ApiModelProperty("服务器状态")
    private String resourcesStatus;
}
