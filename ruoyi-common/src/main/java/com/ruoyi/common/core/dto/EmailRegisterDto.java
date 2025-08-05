package com.ruoyi.common.core.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * []
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmailRegisterDto {

    /**
     * 验证码
     */
    @NotBlank(message = "验证码为空")
    private String code;

    /**
     * 唯一标识
     */
    @NotBlank(message = "唯一标识为空")
    private String uuid;

    /**
     * 邮箱
     */
    @Pattern(
            regexp = "^[a-zA-Z0-9_!#$%&'*+/=?`{|}~^.-]+@[a-zA-Z0-9.-]+$",
            message = "邮箱格式无效"
    )
    @NotBlank(message = "邮箱为空")
    private String email;

    /**
     * 邮箱验证码
     */
    @NotBlank(message = "邮箱验证码为空")
    private String emailCode;
}
