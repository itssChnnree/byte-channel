package com.ruoyi.system.domain.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [密码修改dto]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2026/4/2
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatePasswordDto {
    @ApiModelProperty(value = "旧密码")
    private String oldPassword;
    @ApiModelProperty(value = "新密码")
    private String newPassword;
}

