package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.domain.dto.TicketMainTextQuoteDto;
import com.ruoyi.system.group.InsertGroup;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.ITicketMainTextQuoteService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;

/**
 * [工单正文报价表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-26 23:05:20
 **/
@Api(tags = "工单正文报价表")
@RestController
@RequestMapping("ticketMainTextQuote")
public class TicketMainTextQuoteController {

    @Resource(name = "ticketMainTextQuoteService")
    ITicketMainTextQuoteService ticketMainTextQuoteService;






}
