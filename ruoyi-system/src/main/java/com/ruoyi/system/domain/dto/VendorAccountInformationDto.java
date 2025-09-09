package com.ruoyi.system.domain.dto;

import java.util.Date;

import com.ruoyi.system.domain.base.PageBase;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;


/**
 * 厂商账号信息表(VendorAccountInformation)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:27
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class VendorAccountInformationDto extends PageBase {

    @ApiModelProperty("主键id")
    private String id;

    @ApiModelProperty("所属厂家id")
    @NotBlank(message = "所属厂家id不能为空")
    private String vendorId;

    @ApiModelProperty("账号")
    @NotBlank(message = "账号不能为空")
    private String account;

    @ApiModelProperty("密码")
    @NotBlank(message = "密码不能为空")
    private String password;

    @ApiModelProperty("邮箱")
    private String vendorEmail;

    @ApiModelProperty("手机号")
    private String vendorPhone;

}
