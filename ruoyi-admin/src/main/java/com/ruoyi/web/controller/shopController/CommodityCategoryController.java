package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.domain.dto.CommodityCategoryDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.ICommodityCategoryService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:47
 **/
@Api(tags = "商品类别")
@RestController
@RequestMapping("commodityCategory")

public class CommodityCategoryController {

    @Resource(name = "commodityCategoryService")
    ICommodityCategoryService commodityCategoryService;


    @PostMapping("/insert")
    @ApiOperation("添加商品类别")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result insert(@RequestBody @Validated(InsertGroup.class) CommodityCategoryDto commodityCategoryDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return commodityCategoryService.insert(commodityCategoryDto);
    }

    @PostMapping("/update")
    @ApiOperation("修改商品类别")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody @Validated(UpdateGroup.class) CommodityCategoryDto commodityCategoryDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return commodityCategoryService.update(commodityCategoryDto);
    }

    @DeleteMapping("/deleteByIds")
    @ApiOperation("删除类别")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteByIds(@RequestBody ListDto listDto){
        return commodityCategoryService.deleteByIds(listDto);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询商品类别")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteByIds(@RequestBody CommodityCategoryDto listDto){
        if (listDto.getPageNum()== null|| listDto.getPageNum() < 1L){
            listDto.setPageNum(1L);
        }
        if (listDto.getPageSize()== null|| listDto.getPageSize() < 1L){
            listDto.setPageSize(10L);
        }
        return commodityCategoryService.page(listDto);
    }


}
