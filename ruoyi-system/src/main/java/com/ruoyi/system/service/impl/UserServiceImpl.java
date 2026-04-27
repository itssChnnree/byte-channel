package com.ruoyi.system.service.impl;

import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.LoginUser;
import com.ruoyi.common.core.dto.EmailRegisterDto;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.helper.email.EmailSender;
import com.ruoyi.common.helper.email.EmailConstant;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.dto.ResetPasswordDto;
import com.ruoyi.system.domain.dto.UpdatePasswordDto;
import com.ruoyi.system.domain.vo.UserInfoVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.service.IUserService;
import com.ruoyi.system.service.ISysUserService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 用户服务实现类
 *
 * @author 陈湘岳
 * @date 2026/3/31
 */
@Service
public class UserServiceImpl implements IUserService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private SysUserMapper userMapper;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Resource
    private RedisCache redisCache;

    @Resource
    private EmailSender emailSender;

    /**
     * 获取当前登录用户信息
     */
    @Override
    public UserInfoVo getCurrentUserInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || loginUser.getUser() == null) {
            return null;
        }
        UserInfoVo vo = new UserInfoVo();
        vo.setUserId(loginUser.getUserId());
        vo.setUserName(loginUser.getUser().getUserName());
        vo.setEmail(loginUser.getUser().getEmail());
        return vo;
    }

    /**
     * 修改密码
     */
    @Override
    public Result updatePassword(UpdatePasswordDto updatePasswordDto) {
        String oldPassword = updatePasswordDto.getOldPassword();
        String newPassword = updatePasswordDto.getNewPassword();

        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return Result.fail("用户未登录");
        }

        Long userId = loginUser.getUserId();
        String password = loginUser.getPassword();

        if (!SecurityUtils.matchesPassword(oldPassword, password)) {
            return Result.fail("修改密码失败，旧密码错误");
        }

        if (SecurityUtils.matchesPassword(newPassword, password)) {
            return Result.fail("新密码不能与旧密码相同");
        }

        newPassword = SecurityUtils.encryptPassword(newPassword);
        if (userService.resetUserPwd(userId, newPassword) > 0) {
            return Result.success();
        }

        return Result.fail("修改密码异常，请联系管理员");
    }

    /**
     * 通过邮箱验证码重置密码
     */
    @Override
    public Result resetPasswordByEmail(ResetPasswordDto dto) {
        String email = dto.getEmail();
        String emailCode = dto.getEmailCode();
        String newPassword = dto.getNewPassword();

        // 1. 验证邮箱验证码
        String cacheKey = CacheConstants.EMAIL_CAPTCHA_CODE_KEY + email;
        String cachedCode = redisCache.getCacheObject(cacheKey);

        if (cachedCode == null) {
            return Result.fail("验证码已过期，请重新获取");
        }

        if (!cachedCode.equals(emailCode)) {
            return Result.fail("验证码错误");
        }

        // 2. 根据邮箱查询用户
        SysUser user = userMapper.checkEmailUnique(email);
        if (user == null) {
            return Result.fail("该邮箱未注册");
        }

        // 3. 加密新密码
        String encryptedPassword = SecurityUtils.encryptPassword(newPassword);

        // 4. 更新密码
        if (userService.resetUserPwd(user.getUserId(), encryptedPassword) > 0) {
            // 删除验证码
            redisCache.deleteObject(cacheKey);
            LogEsUtil.info("用户密码重置成功，邮箱：" + email);
            return Result.success("密码重置成功");
        }

        return Result.fail("密码重置失败，请稍后重试");
    }

    /**
     * 获取重置密码邮箱验证码
     */
    @Override
    public Result getResetPasswordEmailCode(EmailRegisterDto emailRegisterDto) {
        String email = emailRegisterDto.getEmail();

        // 1. 校验图形验证码
        validateCaptcha(emailRegisterDto.getCode(), emailRegisterDto.getUuid());

        // 2. 校验邮箱是否已注册(重置密码必须邮箱已注册)
        SysUser user = userMapper.checkEmailUnique(email);
        if (user == null) {
            return Result.fail("该邮箱未注册");
        }

        // 3. 生成邮箱验证码
        try {
            String emailCode = EmailSender.generateVerificationCode(6);
            // 设置验证码过期时间为5分钟
            redisCache.setCacheObject(CacheConstants.EMAIL_CAPTCHA_CODE_KEY + email, emailCode, 5, TimeUnit.MINUTES);

            // 4. 发送邮件
            emailSender.sendVerificationEmail(
                    email,
                    email,
                    emailCode,
                    EmailConstant.RESET_PASSWORD,
                    5
            );

            LogEsUtil.info("重置密码验证码发送成功，邮箱：" + email);
            return Result.success("验证码已发送，请注意查收");
        } catch (Exception e) {
            LogEsUtil.error("发送重置密码验证码邮件失败，邮箱：" + email, e);
            return Result.fail("发送邮件失败，请稍后重试");
        }
    }

    /**
     * 校验图形验证码
     */
    private void validateCaptcha(String code, String uuid) {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + (uuid != null ? uuid : "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);

        if (captcha == null) {
            throw new com.ruoyi.common.exception.user.CaptchaExpireException();
        }

        if (!code.equalsIgnoreCase(captcha)) {
            throw new com.ruoyi.common.exception.user.CaptchaException();
        }
    }
}
