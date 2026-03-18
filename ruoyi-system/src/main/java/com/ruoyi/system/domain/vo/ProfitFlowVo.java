package com.ruoyi.system.domain.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.annotations.ApiModelProperty;


/**
 * 利润流水表(ProfitFlow)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 00:12:32
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfitFlowVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("利润金额（正数收入，负数支出）")
    private Double profitAmount;

    @ApiModelProperty("来源类型")
    private String sourceType;

    @ApiModelProperty("来源业务ID（如订单ID）")
    private String sourceId;

    @ApiModelProperty("来源描述")
    private String sourceDesc;

    @ApiModelProperty("图片URL")
    private String imageUrl;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("0为未删除，1为已删除")
    private Integer isDeleted;

    @ApiModelProperty("创建人")
    private String createUser;

    @ApiModelProperty("修改人")
    private String updateUser;

    @ApiModelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;

    @ApiModelProperty("状态")
    private String status;


}
