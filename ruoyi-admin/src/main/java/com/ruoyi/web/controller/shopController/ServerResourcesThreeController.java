package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.domain.dto.ServerResourcesThreeDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IServerResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [第三方服务器资源上传控制器]
 * 供第三方系统调用，上传vless配置信息到server_resources表
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Api(tags = "第三方服务器资源上传")
@RestController
@RequestMapping("/serverResourcesThree")
public class ServerResourcesThreeController {

    @Resource(name = "serverResourcesService")
    IServerResourcesService serverResourcesService;

    @PostMapping("/insert")
    @ApiOperation("第三方上传资源")
    public Result insert(@RequestBody @Valid ServerResourcesThreeDto dto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return serverResourcesService.insertThree(dto);
    }
}
