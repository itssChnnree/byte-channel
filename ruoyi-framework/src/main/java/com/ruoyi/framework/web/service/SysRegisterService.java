package com.ruoyi.framework.web.service;

import com.ruoyi.common.core.dto.EmailRegisterDto;
import com.ruoyi.common.helper.email.EmailConstant;
import com.ruoyi.common.helper.email.EmailSender;
import com.ruoyi.system.http.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ruoyi.common.constant.CacheConstants;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.constant.UserConstants;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.core.redis.RedisCache;
import com.ruoyi.common.exception.user.CaptchaException;
import com.ruoyi.common.exception.user.CaptchaExpireException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.MessageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.manager.AsyncManager;
import com.ruoyi.framework.manager.factory.AsyncFactory;
import com.ruoyi.system.service.ISysConfigService;
import com.ruoyi.system.service.ISysUserService;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.concurrent.TimeUnit;

/**
 * 注册校验方法
 * 
 * @author ruoyi
 */
@Component
public class SysRegisterService
{
    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysConfigService configService;

    @Autowired
    private RedisCache redisCache;

    @Resource
    private EmailSender emailSender;


    /**
     * 注册
     */
    public String register(RegisterBody registerBody)
    {
        String msg = "", password = registerBody.getPassword();
        SysUser sysUser = new SysUser();
        sysUser.setUserName(registerBody.getEmail());
        sysUser.setEmail(registerBody.getEmail());
        if (!userService.checkEmailUnique(registerBody.getEmail()))
        {
            msg = "保存用户'" + registerBody.getEmail() + "'失败，注册邮箱已存在";
        }
        else
        {
            sysUser.setNickName(registerBody.getEmail());
            sysUser.setPwdUpdateDate(DateUtils.getNowDate());
            sysUser.setPassword(SecurityUtils.encryptPassword(password));
            boolean regFlag = userService.registerUser(sysUser);
            if (!regFlag)
            {
                msg = "注册失败,请联系系统管理人员";
            }
            else
            {
                AsyncManager.me().execute(AsyncFactory.recordLogininfor(sysUser.getUserName(), Constants.REGISTER, MessageUtils.message("user.register.success")));
            }
        }
        return msg;
    }

    /**
     * 校验验证码
     * 
     * @param username 用户名
     * @param code 验证码
     * @param uuid 唯一标识
     * @return 结果
     */
    public void validateCaptcha(String username, String code, String uuid)
    {
        String verifyKey = CacheConstants.CAPTCHA_CODE_KEY + StringUtils.nvl(uuid, "");
        String captcha = redisCache.getCacheObject(verifyKey);
        redisCache.deleteObject(verifyKey);
        if (captcha == null)
        {
            throw new CaptchaExpireException();
        }
        if (!code.equalsIgnoreCase(captcha))
        {
            throw new CaptchaException();
        }
    }

    public void validateCaptcha( String code, String uuid){
        validateCaptcha(null,code,uuid);
    }


    /**
     * [获取邮箱验证码]
     * @author 陈湘岳 2025/7/26
     * @param emailRegisterDto
     * @return com.ruoyi.system.http.Result
     **/
    public Result getEmailCode(@Valid EmailRegisterDto emailRegisterDto) {
        //校验验证码
        if (configService.selectCaptchaEnabled())
        {
            validateCaptcha( emailRegisterDto.getCode(), emailRegisterDto.getUuid());
        }
        //校验邮箱是否被注册
        boolean hasEmail = userService.checkEmailUnique(emailRegisterDto.getEmail());
        if(!hasEmail){
            return Result.fail("邮箱已被注册");
        }
        try {
            String emailCode = EmailSender.generateVerificationCode(6);
            //设置验证码过期时间
            redisCache.setCacheObject(CacheConstants.EMAIL_CAPTCHA_CODE_KEY+emailRegisterDto.getEmail(),emailCode,5, TimeUnit.MINUTES);
            emailSender.sendVerificationEmail(emailRegisterDto.getEmail(),emailRegisterDto.getEmail(),
                    emailCode, EmailConstant.REGISTER,5);
        } catch (MessagingException e) {
            throw new RuntimeException("发送邮件失败");
        }
        return Result.success("发送成功");
    }
}
