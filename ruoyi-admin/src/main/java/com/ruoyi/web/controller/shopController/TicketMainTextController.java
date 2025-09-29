package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.service.ITicketMainTextService;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [工单正文表]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:36
 **/
@Api(tags = "工单正文表")
@RestController
@RequestMapping("ticketMainText")
public class TicketMainTextController   {

    @Resource(name = "ticketMainTextService")
    ITicketMainTextService ticketMainTextService;



}
