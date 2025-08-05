package com.ruoyi.web.controller.shopController;

import com.ruoyi.system.service.IPromoRecordsService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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


}
