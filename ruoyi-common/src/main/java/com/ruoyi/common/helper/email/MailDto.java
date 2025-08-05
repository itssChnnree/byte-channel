package com.ruoyi.common.helper.email;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * [邮箱发送]
 *
 * @author 陈湘岳
 * @version v1.0.0
 * @date 2025/7/26
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDto {

    //邮箱
    private String email;

    //code
    private String code;

    //操作
    private String operation;
}
