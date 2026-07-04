package com.ruoyi.web.controller.shopController;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.Socks5ResourcesDto;
import com.ruoyi.system.domain.dto.Socks5ResourcesInsertDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.ISocks5ResourcesService;
import com.ruoyi.system.domain.vo.Socks5ResourcesVo;
import com.ruoyi.system.domain.entity.Socks5Resources;
import com.ruoyi.system.mapstruct.Socks5ResourcesMapstruct;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [socks5资源记录控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-06-29 22:23:02
 **/
@Api(tags = "socks5资源记录")
@RestController
@RequestMapping("socks5Resources")
public class Socks5ResourcesController {

    @Resource(name = "socks5ResourcesService")
    ISocks5ResourcesService socks5ResourcesService;

    @PostMapping("/insert")
    @ApiOperation("第三方上传socks5配置")
    public Result insert(@RequestBody @Valid Socks5ResourcesInsertDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return socks5ResourcesService.insertSocks5(dto);
    }

    @GetMapping("/queryByPassword")
    @ApiOperation("通过密码查询socks5配置")
    public Result queryByPassword(String password) {
        return socks5ResourcesService.getByPassword(password);
    }

}
