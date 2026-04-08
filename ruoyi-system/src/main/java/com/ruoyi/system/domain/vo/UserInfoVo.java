package com.ruoyi.system.domain.vo;

import lombok.Data;

/**
 * 用户信息VO
 *
 * @author 陈湘岳
 * @date 2026/3/31
 */
@Data
public class UserInfoVo {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String userName;


    /**
     * 邮箱
     */
    private String email;


}
