package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.dto.EmailRegisterDto;
import com.ruoyi.system.domain.dto.ResetPasswordDto;
import com.ruoyi.system.domain.dto.UpdatePasswordDto;
import com.ruoyi.system.domain.dto.UserConfigDto;
import com.ruoyi.system.domain.vo.UserConfigVo;
import com.ruoyi.system.domain.vo.UserInfoVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IUserConfigService;
import com.ruoyi.system.service.IUserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 用户中心控制器
 *
 * @author 陈湘岳
 * @date 2026/3/31
 */
@Api(tags = "用户中心")
@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private IUserService userService;

    @Resource
    private IUserConfigService userConfigService;

    @Resource
    private com.ruoyi.framework.web.service.SysRegisterService registerService;

    @Resource
    private com.ruoyi.framework.web.service.TokenService tokenService;

    /**
     * 获取当前登录用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/getUserInfo")
    public Result getUserInfo() {
        UserInfoVo userInfo = userService.getCurrentUserInfo();
        return Result.success(userInfo);
    }

    /**
     * 修改密码
     */
    @ApiOperation("修改密码")
    @PutMapping("/updatePwd")
    public Result updatePwd(@RequestBody UpdatePasswordDto updatePasswordDto) {
        if (updatePasswordDto == null){
            return Result.fail("参数不能为空");
        }
        if (StrUtil.isBlank(updatePasswordDto.getOldPassword()) || StrUtil.isBlank(updatePasswordDto.getNewPassword())){
            return Result.fail("参数不能为空");
        }
        return userService.updatePassword(updatePasswordDto);
    }

    /**
     * 获取用户配置（邮件提醒设置）
     */
    @ApiOperation("获取用户配置")
    @GetMapping("/getUserConfig")
    public Result getUserConfig() {
        UserConfigVo config = userConfigService.getCurrentUserConfig();
        return Result.success(config);
    }

    /**
     * 更新用户配置（邮件提醒设置）
     */
    @ApiOperation("更新用户配置")
    @PostMapping("/updateUserConfig")
    public Result updateUserConfig(@RequestBody UserConfigDto dto) {
        userConfigService.updateCurrentUserConfig(dto);
        return Result.success("更新成功");
    }

    /**
     * 获取重置密码邮箱验证码
     */
    @ApiOperation("获取重置密码邮箱验证码")
    @PostMapping("/getResetPwdEmailCode")
    public Result getResetPwdEmailCode(@RequestBody @Valid EmailRegisterDto emailRegisterDto, 
                                       org.springframework.validation.BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return userService.getResetPasswordEmailCode(emailRegisterDto);
    }

    /**
     * 重置密码（通过邮箱验证码）
     */
    @ApiOperation("重置密码")
    @PostMapping("/resetPassword")
    public Result resetPassword(@Valid @RequestBody ResetPasswordDto dto) {
        return userService.resetPasswordByEmail(dto);
    }
}
