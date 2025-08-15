package com.ruoyi.web.controller.shopController;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IPromoCodeRecordsService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:14
 **/
@Api(tags = "推广码记录表")
@RestController
@RequestMapping("promoCodeRecords")
public class PromoCodeRecordsController extends BaseController {

    @Resource(name = "promoCodeRecordsService")
    IPromoCodeRecordsService promoCodeRecordsService;


    @PostMapping("/createPromoCodeRecords")
    @ApiOperation("生成推广码")
    public Result createPromoCodeRecords() {
        return promoCodeRecordsService.createPromoCodeRecords();
    }


}
