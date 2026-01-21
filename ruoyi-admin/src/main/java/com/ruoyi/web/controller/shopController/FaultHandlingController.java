package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.domain.dto.FaultHandlingDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.group.UpdateGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IFaultHandlingService;
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
 * @date 2025-07-20 23:23:54
 **/
@Api(tags = "故障处理流程表")
@RestController
@RequestMapping("faultHandling")

public class FaultHandlingController{

    @Resource(name = "faultHandlingService")
    IFaultHandlingService faultHandlingService;

    @PostMapping("/insert")
    @ApiOperation("添加故障处理流程")
    public Result insert(@RequestBody @Validated(InsertGroup.class) FaultHandlingDto faultHandlingDto,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return faultHandlingService.insert(faultHandlingDto);
    }

    @PostMapping("/update")
    @ApiOperation("修改故障处理流程")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody @Validated(UpdateGroup.class) FaultHandlingDto faultHandlingDto,
                         BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return faultHandlingService.update(faultHandlingDto);
    }


    @PostMapping("/deleteByIds")
    @ApiOperation("删除故障处理流程")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteByIds(@RequestBody ListDto listDto){
        return faultHandlingService.deleteByIds(listDto);
    }


    @PostMapping("/pageQuery")
    @ApiOperation("查询故障处理流程")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result pageQuery(@RequestBody FaultHandlingDto faultHandlingDto){
        return faultHandlingService.pageQuery(faultHandlingDto);
    }



}
