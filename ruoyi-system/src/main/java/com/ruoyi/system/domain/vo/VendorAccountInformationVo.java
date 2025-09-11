package com.ruoyi.system.domain.vo;

import java.util.Date;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * 厂商账号信息表(VendorAccountInformation)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:24
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorAccountInformationVo {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("所属厂家id")
    private String vendorId;

    @ApiModelProperty("账号")
    private String account;

    @ApiModelProperty("密码")
    private String password;

    @ApiModelProperty("邮箱")
    private String vendorEmail;

    @ApiModelProperty("手机号")
    private String vendorPhone;

    @ApiModelProperty("厂商名")
    private String vendorName;

    @ApiModelProperty("厂商网址")
    private String vendorUrl;

    @ApiModelProperty("厂商描述")
    private String vendorDescription;

    @ApiModelProperty("管理人")
    private String manager;

}
