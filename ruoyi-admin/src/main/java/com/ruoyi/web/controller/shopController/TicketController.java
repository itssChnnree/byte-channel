package com.ruoyi.web.controller.shopController;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.service.ITicketService;
import com.ruoyi.system.domain.entity.Ticket;
import com.ruoyi.system.mapstruct.TicketMapstruct;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;

/**
 * [工单主表]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:35
 **/
@Api(tags = "工单主表")
@RestController
@RequestMapping("ticket")
public class TicketController   {

    @Resource(name = "ticketService")
    ITicketService ticketService;


}
