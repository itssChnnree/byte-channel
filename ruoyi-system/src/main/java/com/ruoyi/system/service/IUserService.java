package com.ruoyi.system.service;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.dto.ResetPasswordDto;
import com.ruoyi.system.domain.dto.UpdatePasswordDto;
import com.ruoyi.system.domain.vo.UserInfoVo;
import com.ruoyi.system.http.Result;

import java.util.Map;

/**
 * 用户服务接口
 *
 * @author 陈湘岳
 * @date 2026/3/31
 */
public interface IUserService {

    /**
     * 获取当前登录用户信息
     *
     * @return 用户信息VO
     */
    UserInfoVo getCurrentUserInfo();

    /**
     * 修改密码
     *
     * @param updatePasswordDto 包含 oldPassword 和 newPassword
     * @return AjaxResult
     */
    Result updatePassword(UpdatePasswordDto updatePasswordDto);

    /**
     * 通过邮箱验证码重置密码
     *
     * @param dto 重置密码DTO
     * @return Result
     */
    Result resetPasswordByEmail(ResetPasswordDto dto);
}
