package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.CommodityDto;
import com.ruoyi.system.domain.dto.IdDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.ICommodityService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:34
 **/
@Api(tags = "商品表")
@RestController
@RequestMapping("commodity")
public class CommodityController {

    @Resource(name = "commodityService")
    ICommodityService commodityService;


    @PostMapping("/insert")
    @ApiOperation("添加商品")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result insert(@RequestBody @Validated(InsertGroup.class) CommodityDto commodityDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return commodityService.insert(commodityDto);
    }

    @PostMapping("/update")
    @ApiOperation("修改商品")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody @Validated(UpdateGroup.class) CommodityDto commodityDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return commodityService.update(commodityDto);
    }

    @PostMapping("/deleteByIds")
    @ApiOperation("删除商品")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteByIds(@RequestBody  ListDto listDto){
        return commodityService.deleteByIds(listDto);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询商品")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(CommodityDto commodityDto){
        if (commodityDto.getPageNum()== null|| commodityDto.getPageNum() < 1L){
            commodityDto.setPageNum(1L);
        }
        if (commodityDto.getPageSize()== null|| commodityDto.getPageSize() < 1L){
            commodityDto.setPageSize(10L);
        }
        return commodityService.page(commodityDto);
    }

    @GetMapping("/findById")
    @ApiOperation("查询商品详情")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result findById(String id){
        if (StrUtil.isBlank( id)){
            return Result.fail("商品编号不能为空");
        }
        return commodityService.findById(id);
    }


    @ApiOperation("商品上（下）架")
    @PutMapping("/updateAvailableStatus")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result updateAvailableStatus(@RequestBody IdDto commodityIdDto ){
        String commodityId = commodityIdDto.getId();
        if (StrUtil.isBlank(commodityId)){
            return Result.fail("商品编号不能为空");
        }
        return commodityService.updateAvailableStatus(commodityId);
    }


    @GetMapping("/userPage")
    @ApiOperation("用户分页查询商品")
    public Result userPage(CommodityDto commodityDto){
        if (commodityDto.getPageNum()== null|| commodityDto.getPageNum() < 1L){
            commodityDto.setPageNum(1L);
        }
        if (commodityDto.getPageSize()== null|| commodityDto.getPageSize() < 1L){
            commodityDto.setPageSize(10L);
        }
        return commodityService.userPage(commodityDto);
    }


}
