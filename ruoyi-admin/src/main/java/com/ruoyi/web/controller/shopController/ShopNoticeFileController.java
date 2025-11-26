package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.IShopNoticeFileService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [公告附件表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:29
 **/
@Api(tags = "公告附件表")
@RestController
@RequestMapping("shopNoticeFile")
public class ShopNoticeFileController {

    @Resource(name = "shopNoticeFileService")
    IShopNoticeFileService shopNoticeFileService;


}
