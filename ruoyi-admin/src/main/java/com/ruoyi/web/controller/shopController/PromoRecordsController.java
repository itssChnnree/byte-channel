package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.domain.dto.PromoRecordsDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IPromoRecordsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:20
 **/
@Api(tags = "推广记录表")
@RestController
@RequestMapping("promoRecords")
public class PromoRecordsController{

    @Resource(name = "promoRecordsService")
    IPromoRecordsService promoRecordsService;


    @ApiOperation("获取返现金额")
    @GetMapping("/getReturnCash")
    public Result getReturnCash(){
        return promoRecordsService.getReturnCash();
    }


    @ApiOperation("获取邀请人数")
    @GetMapping("/getReturnPeopleNum")
    public Result getReturnPeopleNum(){
        return promoRecordsService.getReturnPeopleNum();
    }


    @PostMapping("/getRecords")
    @ApiOperation("获取邀请记录")
    public Result getRecords(@RequestBody PromoRecordsDto promoRecordsDto){
        return promoRecordsService.getRecords(promoRecordsDto);
    }


}
