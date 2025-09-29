package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.ITicketMainTextFileService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [工单正文文件附件表]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:37
 **/
@Api(tags = "工单正文文件附件表")
@RestController
@RequestMapping("ticketMainTextFile")
public class TicketMainTextFileController   {

    @Resource(name = "ticketMainTextFileService")
    ITicketMainTextFileService ticketMainTextFileService;


}
