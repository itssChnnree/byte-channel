package com.ruoyi.system.domain.dto;

import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;


/**
 * 服务器资源表(ServerResources)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ServerResourcesPageDto extends PageBase {

    private String resourcesIp;


    @ApiModelProperty("服务器状态")
    private String resourcesStatus;
    
    @ApiModelProperty("售卖状态")
    private String salesStatus;
    
    @ApiModelProperty("可售状态，可售状态,0为下架，1为上架")
    @NotNull(message = "可售状态不能为空")
    private Integer availableStatus;

    @ApiModelProperty("资源所属云服务商id")
    private String vendorId;

    @ApiModelProperty("资源所属云服务商账号id")
    private String vendorAccountId;

    
    @ApiModelProperty("所属商品id")
    @NotBlank(message = "所属商品id不可为空")
    private String commodityId;

    @ApiModelProperty("商品分类")
    private String commodityCategoryId;

    @ApiModelProperty("到期时间类型")
    //到期时间类型,1为7天内到期，2为15天，3为一月
    private Integer expireTimeType;

    @ApiModelProperty("是否开启续费")
    //0为未开启，1为已开启
    private Integer isOpenRenewal;
}
