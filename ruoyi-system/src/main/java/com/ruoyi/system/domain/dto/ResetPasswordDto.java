package com.ruoyi.system.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 重置密码请求DTO
 *
 * @author 陈湘岳
 * @date 2026/3/31
 */
@Data
public class ResetPasswordDto {

    /**
     * 邮箱地址
     */
    @NotBlank(message = "邮箱不能为空")
    private String email;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String emailCode;

    /**
     * 新密码
     */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, message = "密码长度不能少于6位")
    private String newPassword;
}
