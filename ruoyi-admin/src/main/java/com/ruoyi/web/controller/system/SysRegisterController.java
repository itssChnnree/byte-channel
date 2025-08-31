package com.ruoyi.web.controller.system;

import com.ruoyi.common.core.dto.EmailRegisterDto;
import com.ruoyi.system.http.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.model.RegisterBody;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.framework.web.service.SysRegisterService;
import com.ruoyi.system.service.ISysConfigService;

import javax.validation.Valid;

/**
 * 注册验证
 * 
 * @author ruoyi
 */
@RestController
public class SysRegisterController extends BaseController
{
    @Autowired
    private SysRegisterService registerService;

    @Autowired
    private ISysConfigService configService;

    @PostMapping("/register")
    public AjaxResult register(@RequestBody @Valid RegisterBody user, BindingResult bindingResult)
    {
        if (bindingResult.hasErrors())
        {
            return error(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return error("注册功能暂不可用");
        }
        String msg = registerService.register(user);
        return StringUtils.isEmpty(msg) ? success() : error(msg);
    }

    @PostMapping("/getEmailCode")
    @ApiOperation("获取邮箱验证码")
    public Result getEmailCode(@RequestBody @Valid EmailRegisterDto emailRegisterDto, BindingResult bindingResult)
    {

        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (!("true".equals(configService.selectConfigByKey("sys.account.registerUser"))))
        {
            return Result.fail("当前系统没有开启注册功能！");
        }
        return registerService.getEmailCode(emailRegisterDto);
    }

}
