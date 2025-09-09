package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.VendorInformationDto;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.domain.entity.VendorInformation;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapstruct.VendorAccountInformationMapstruct;
import com.ruoyi.system.mapstruct.VendorInformationMapstruct;
import com.ruoyi.system.service.IVendorInformationService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:34
 **/
@Api(tags = "厂商信息表")
@RestController
@RequestMapping("/vendorInformation")
public class VendorInformationController {

    @Resource
    IVendorInformationService vendorInformationService;
    

    /**
     * [分页查询]
     * @author 陈湘岳 2025/9/8
     * @param vendorInformationDto 分页查询入参
     * @return com.ruoyi.system.http.Result
     **/
    @ApiOperation("分页查询")
    @GetMapping(value = "/list")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result list(VendorInformationDto vendorInformationDto) {
        return vendorInformationService.queryList(vendorInformationDto);
    }



    /**
     * [新增]
     * @author 陈湘岳 2025/9/8
     * @param vendorInformationDto 新增入参
     * @return com.ruoyi.system.http.Result
     **/
    @ApiOperation("新增")
    @PostMapping("/insert")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result insert(@RequestBody @Valid VendorInformationDto vendorInformationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        VendorInformation vendorInformation = VendorInformationMapstruct.INSTANCE.changeDto2(vendorInformationDto);
        return this.vendorInformationService.save(vendorInformation);
    }

    @DeleteMapping("/deleteById")
    @ApiOperation("删除云厂商")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteByIds(String id){
        return vendorInformationService.deleteById(id);
    }



    /**
     * [更新]
     * @author 陈湘岳 2025/9/8
     * @param vendorInformationDto 更新入参
     * @return com.ruoyi.system.http.Result
     **/
    @ApiOperation("更新")
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result put(@RequestBody @Valid VendorInformationDto vendorInformationDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (StrUtil.isBlank( vendorInformationDto.getId())){
            return Result.fail("修改账号id为空，请联系无敌的陈湘岳");
        }
        VendorInformation vendorInformation = VendorInformationMapstruct.INSTANCE.changeDto2(vendorInformationDto);
        return this.vendorInformationService.updateById(vendorInformation);
    }


}
