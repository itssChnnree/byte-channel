package com.ruoyi.system.domain.dto.log;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 *      日志采集信息
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@ApiModel("日志采集信息")
public class LogCollectionInfoDTO {

    /** 日志采集主键ID */
    @ApiModelProperty(value = "日志采集主键ID")
    private Integer logCollectionId;
    /** 日志采集任务名称 */
    @ApiModelProperty(value = "日志采集任务名称",notes = "")
    private String logCollectionName;
    /** 目标主机ip */
    @ApiModelProperty(value = "目标主机ip",notes = "")
    private String host;
    /** 目标主机端口 */
    @ApiModelProperty(value = "目标主机端口",notes = "")
    private Integer port;
    /** 目标主机账号   */
    @ApiModelProperty(value = "目标主机账号",notes = "")
    private String account;
    /** 目标主机密码 */
    @ApiModelProperty(value = "目标主机密码",notes = "")
    private String pwd;
    /** 日志采集路径 */
    @ApiModelProperty(value = "日志采集路径",notes = "")
    private String directory;
    /** 租户号 */
    @ApiModelProperty(value = "租户号",notes = "")
    private String tenantId;
    /** 创建人 */
    @ApiModelProperty(value = "创建人",notes = "")
    private Integer createStaff;
    /** 创建时间 */
    @ApiModelProperty(value = "创建时间",notes = "")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date createDate;
    /** 更新人 */
    @ApiModelProperty(value = "更新人",notes = "")
    private Integer updateStaff;
    /** 更新时间 */
    @ApiModelProperty(value = "更新时间",notes = "")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date updateDate;
    /** 状态 */
    @ApiModelProperty(value = "状态",notes = "")
    private String statusCd;
    /** 状态时间 */
    @ApiModelProperty(value = "状态时间",notes = "")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private Date statusDate;
    /** 备注 */
    @ApiModelProperty(value = "备注",notes = "")
    private String remark;


}