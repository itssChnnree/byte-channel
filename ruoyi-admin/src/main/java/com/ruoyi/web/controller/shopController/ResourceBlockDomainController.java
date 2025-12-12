package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.ResourceBlockDomainDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IResourceBlockDomainService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Date;

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
    public Result add(@RequestBody @Valid ResourceBlockDomainDto resourceBlockDomain, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return resourceBlockDomainService.add(resourceBlockDomain);
    }


    @DeleteMapping("/deleteById")
    @ApiOperation("删除屏蔽域名")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteById(String id, Date scheduleTime, Boolean isAddToRecentUpdatePlan) {
        if (StrUtil.isBlank(id)){
            return Result.fail("请选择移除屏蔽的域名");
        }
        return resourceBlockDomainService.deleteById(id,scheduleTime,isAddToRecentUpdatePlan);
    }


    @GetMapping("/list")
    @ApiOperation("查询屏蔽域名列表")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result list(ResourceBlockDomainDto resourceBlockDomainDto) {
        if (resourceBlockDomainDto.getPageNum() == null || resourceBlockDomainDto.getPageNum() < 1){
            resourceBlockDomainDto.setPageNum(1L);
        }
        if (resourceBlockDomainDto.getPageSize() == null || resourceBlockDomainDto.getPageSize() < 1){
            resourceBlockDomainDto.setPageSize(10L);
        }
        return resourceBlockDomainService.list(resourceBlockDomainDto);
    }



    @PostMapping("/update")
    @ApiOperation("更新屏蔽域名")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody ResourceBlockDomainDto resourceBlockDomain) {
        if (StrUtil.isBlank(resourceBlockDomain.getId())){
            return Result.fail("请选择要更新的屏蔽域名");
        }
        return resourceBlockDomainService.update(resourceBlockDomain);
    }
}
