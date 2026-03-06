package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.ServerResourcesRenewalDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IServerResourcesRenewalService;
import com.ruoyi.system.util.LogEsUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [服务器资源自动续费表]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-28 10:48:27
 **/
@Api(tags = "服务器资源自动续费表")
@RestController
@RequestMapping("serverResourcesRenewal")
public class ServerResourcesRenewalController{

    @Resource(name = "serverResourcesRenewalService")
    IServerResourcesRenewalService serverResourcesRenewalService;


    @PostMapping("/insertOrUpdate")
    @ApiOperation("添加或修改自动续费配置")
    public Result insertOrUpdate(@RequestBody ServerResourcesRenewalDto serverResourcesRenewalDto){
        if (StrUtil.isEmpty(serverResourcesRenewalDto.getResourcesId())){
            LogEsUtil.warn("用户未选择自动续费配置资源");
            return Result.fail("请选择资源");
        }
        return serverResourcesRenewalService.insertOrUpdate(serverResourcesRenewalDto);
    }

    @GetMapping("/getByResourcesId")
    @ApiOperation("根据资源id查询")
    public Result getByResourcesId( String resourcesId ){
        return serverResourcesRenewalService.getByResourcesId(resourcesId);
    }
}
