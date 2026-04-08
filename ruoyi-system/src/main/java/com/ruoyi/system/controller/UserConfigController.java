package com.ruoyi.system.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.UserConfigDto;
import com.ruoyi.system.service.IUserConfigService;
import com.ruoyi.system.domain.vo.UserConfigVo;
import com.ruoyi.system.domain.entity.UserConfig;
import com.ruoyi.system.mapstruct.UserConfigMapstruct;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/**
 * [用户配置表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-04-01 23:27:10
 **/
@Api(tags = "用户配置表")
@RestController
@RequestMapping("userConfig")
public class UserConfigController {

    @Resource(name = "userConfigService")
    IUserConfigService userConfigService;


}
