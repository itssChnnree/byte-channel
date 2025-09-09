package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.VendorAccountInformationDto;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapstruct.VendorAccountInformationMapstruct;
import com.ruoyi.system.service.IVendorAccountInformationService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:33
 **/
@Api(tags = "厂商账号信息表")
@RestController
@RequestMapping("vendorAccountInformation")
public class VendorAccountInformationController{

    @Resource(name = "vendorAccountInformationService")
    IVendorAccountInformationService vendorAccountInformationService;

    /**
     * [详情查询]
     * @author 陈湘岳 2025/9/8
     * @param id 账号id
     * @return com.ruoyi.system.http.Result
     **/
    @ApiOperation("详情查询")
    @GetMapping(value = "/findById")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getById(String id) {
        if (StrUtil.isBlank( id)){
            return Result.fail("所选账号id为空");
        }
        return vendorAccountInformationService.queryById(id);
    }

    /**
     * [分页查询]
     * @author 陈湘岳 2025/9/8
     * @param vendorAccountInformationDto 分页查询入参
     * @return com.ruoyi.system.http.Result
     **/
    @ApiOperation("分页查询")
    @GetMapping(value = "/list")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result list(VendorAccountInformationDto vendorAccountInformationDto) {
        return vendorAccountInformationService.queryList(vendorAccountInformationDto);
    }



    /**
     * [新增]
     * @author 陈湘岳 2025/9/8
     * @param vendorAccountInformationDTO 新增入参
     * @return com.ruoyi.system.http.Result
     **/
    @ApiOperation("新增")
    @PostMapping("/insert")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result insert(@RequestBody @Valid VendorAccountInformationDto vendorAccountInformationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        VendorAccountInformation vendorAccountInformation = VendorAccountInformationMapstruct.INSTANCE.changeDto2(vendorAccountInformationDTO);
        return this.vendorAccountInformationService.save(vendorAccountInformation);
    }

    @DeleteMapping("/deleteById")
    @ApiOperation("删除类别")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteByIds(String id){
        return vendorAccountInformationService.deleteById(id);
    }



    /**
     * [更新]
     * @author 陈湘岳 2025/9/8
     * @param vendorAccountInformationDTO 更新入参
     * @return com.ruoyi.system.http.Result
     **/
    @ApiOperation("更新")
    @PutMapping("/update")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result put(@RequestBody @Valid VendorAccountInformationDto vendorAccountInformationDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        if (StrUtil.isBlank( vendorAccountInformationDTO.getId())){
            return Result.fail("修改账号id为空，请联系无敌的陈湘岳");
        }
        VendorAccountInformation vendorAccountInformation = VendorAccountInformationMapstruct.INSTANCE.changeDto2(vendorAccountInformationDTO);
        return this.vendorAccountInformationService.updateById(vendorAccountInformation);
    }

}
