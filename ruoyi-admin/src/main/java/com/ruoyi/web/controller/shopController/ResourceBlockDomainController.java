package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.domain.dto.ResourceBlockDomainDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IResourceBlockDomainService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [资源屏蔽域名表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 13:40:07
 **/
@Api(tags = "资源屏蔽域名表")
@RestController
@RequestMapping("resourceBlockDomain")
public class ResourceBlockDomainController {

    @Resource(name = "resourceBlockDomainService")
    IResourceBlockDomainService resourceBlockDomainService;


    @PostMapping("/add")
    @ApiOperation("添加屏蔽域名")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result add(@RequestBody ResourceBlockDomainDto resourceBlockDomain) {
        return resourceBlockDomainService.add(resourceBlockDomain);
    }

}
