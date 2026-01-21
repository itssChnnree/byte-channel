package com.ruoyi.framework.web.service;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.dto.EmailRegisterDto;
import com.ruoyi.common.helper.email.EmailConstant;
import com.ruoyi.common.helper.email.EmailSender;
import com.ruoyi.system.constant.EntityStatus;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import com.ruoyi.system.domain.entity.WalletBalance;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.PromoCodeRecordsMapper;
import com.ruoyi.system.mapper.WalletBalanceMapper;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.validation.Valid;
import java.math.BigDecimal;
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

    @Resource
    private WalletBalanceMapper walletBalanceMapper;

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;


    /**
     * 注册
     */
    @Transactional
    public String register(RegisterBody registerBody)
    {
        String msg = "", password = registerBody.getPassword();
        String validEmail = validEmail(registerBody.getEmail(), registerBody.getEmailCode());
        if (validEmail != null){
            return validEmail;
        }
        SysUser sysUser = new SysUser();
        sysUser.setUserName(registerBody.getEmail());
        sysUser.setEmail(registerBody.getEmail());
        if (!userService.checkEmailUnique(registerBody.getEmail()))
        {
            msg = "邮箱已被注册，请登录";
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
                //创建余额记录
                WalletBalance walletBalance = getWalletBalance(sysUser);
                walletBalanceMapper.insert(walletBalance);
                promoCodeRecordsMapper.insert(getPromoCodeRecords(sysUser));
            }
        }
        return msg;
    }

    private  WalletBalance getWalletBalance(SysUser sysUser) {
        WalletBalance walletBalance = new WalletBalance();
        walletBalance.setUserId(sysUser.getUserId().toString());
        walletBalance.setInviteesNumber(0);
        walletBalance.setCreateUser(sysUser.getUserId().toString());
        walletBalance.setUpdateUser(sysUser.getUserId().toString());
        walletBalance.setBalance(BigDecimal.valueOf(0.0));
        return walletBalance;
    }

    private PromoCodeRecords getPromoCodeRecords(SysUser sysUser) {
        PromoCodeRecords promoCodeRecords = new PromoCodeRecords();
        promoCodeRecords.setUserId(sysUser.getUserId().toString());
        promoCodeRecords.setStatus(EntityStatus.NORMAL);
        return promoCodeRecords;
    }


    private String validEmail(String email,String code) {
        String cacheObject = redisCache.getCacheObject(CacheConstants.EMAIL_CAPTCHA_CODE_KEY + email);
        if (StrUtil.isBlank(cacheObject)){
            return "验证码已过期";
        }
        if (!code.equals(cacheObject)){
            return "验证码错误";
        }
        return null;
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
