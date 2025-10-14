package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.framework.web.service.PermissionService;
import com.ruoyi.system.domain.dto.IdDto;
import com.ruoyi.system.domain.dto.TicketDto;
import com.ruoyi.system.domain.dto.TicketMainTextDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.ITicketService;
import com.ruoyi.system.domain.entity.Ticket;
import com.ruoyi.system.mapstruct.TicketMapstruct;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.annotation.Resource;
import javax.validation.Valid;

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

    @Resource
    private PermissionService permissionService;


    @PostMapping("/insert")
    @ApiOperation("用户首次提交工单")
    public Result insert(@RequestBody @Valid TicketDto ticketDto) {
        return ticketService.insert(ticketDto);
    }


    @PostMapping("/replyTicket")
    @ApiOperation("用户回复工单")
    public Result replyTicket(@RequestBody @Valid TicketMainTextDto ticketDto) {
        return ticketService.replyTicket(ticketDto);
    }

    @PostMapping("/serviceReplyTicket")
    @ApiOperation("客服回复工单")
    public Result serviceReplyTicket(@RequestBody @Valid TicketMainTextDto ticketDto) {
        return ticketService.serviceReplyTicket(ticketDto);
    }

    @PutMapping("/closeTicket")
    @ApiOperation("关闭工单")
    public Result closeTicket(@RequestBody IdDto idDto) {
        if (StrUtil.isBlank(idDto.getId())){
            return Result.fail("工单编号不能为空");
        }
        return ticketService.closeTicket(idDto.getId());
    }


    @ApiOperation("获取是否有需要用户回复的工单")
    @GetMapping("/getNeedUserReply")
    public Result getNeedUserReply() {
        return ticketService.getNeedUserReply();
    }

    @ApiOperation("获取是否有需要客服回复的工单")
    @GetMapping("/getNeedServiceReply")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getNeedServiceReply() {
        return ticketService.getNeedServiceReply();
    }


    @ApiOperation("获取用户工单列表")
    @GetMapping("/getUserTicketList")
    public Result getUserTicketList(TicketDto ticketDto){
        return ticketService.getUserTicketList(ticketDto);
    }

    @ApiOperation("获取客服工单列表")
    @GetMapping("/getServiceTicketList")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getServiceTicketList(TicketDto ticketDto){
        return ticketService.getServiceTicketList(ticketDto);
    }


    @ApiOperation("查询工单详情")
    @GetMapping("/getTicketDetails")
    public Result getTicketDetails(String id,Integer pageNum,Integer pageSize){
        if (StrUtil.isBlank(id)){
            return Result.fail("工单编号不能为空");
        }
        if(pageNum == null||pageNum<=0){
            pageNum = 1;
        }
        if(pageSize == null||pageSize<=0){
            pageSize = 10;
        }
        boolean hasPermi = permissionService.hasPermi("shop:background:admin");
        return ticketService.getTicketDetails(id,hasPermi,pageNum,pageSize);
    }

}
