package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.TicketStatus;
import com.ruoyi.system.domain.dto.TicketDto;
import com.ruoyi.system.domain.dto.TicketMainTextDto;
import com.ruoyi.system.domain.entity.Ticket;
import com.ruoyi.system.domain.entity.TicketMainText;
import com.ruoyi.system.domain.entity.TicketMainTextFile;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.SysUserMapper;
import com.ruoyi.system.mapper.TicketMainTextFileMapper;
import com.ruoyi.system.mapper.TicketMainTextMapper;
import com.ruoyi.system.mapper.TicketMapper;
import com.ruoyi.system.mapstruct.TicketMapstruct;
import com.ruoyi.system.service.ITicketService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 工单主表(Ticket)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:35
 */
@Service("ticketService")
public class TicketServiceImpl  implements ITicketService {

    @Resource
    private TicketMapstruct ticketMapstruct;


    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private SysUserMapper  sysUserMapper;


    @Resource
    private TicketMainTextMapper ticketMainTextMapper;

    @Resource
    private TicketMainTextFileMapper ticketMainTextFileMapper;

    /**
     * [用户提交工单]
     *
     * @param ticketDto 工单信息
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/29
     **/
    @Override
    @Transactional
    public Result insert(TicketDto ticketDto) {
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        Ticket ticket = ticketMapstruct.changeDto2(ticketDto);
        ticket.setStatus(TicketStatus.NEW);
        ticket.setUserId(SecurityUtils.getStrUserId());
        ticketMapper.insert(ticket);
        TicketMainText ticketMainText = new TicketMainText();
        ticketMainText.setTicketId(ticket.getId());
        ticketMainText.setUserName(sysUser.getNickName());
        ticketMainText.setTicketMainText(ticketDto.getTicketMainText());
        ticketMainText.setUserId(SecurityUtils.getStrUserId());
        ticketMainTextMapper.insert(ticketMainText);
        //如果不为空新增文件
        if (!CollectionUtils.isEmpty(ticketDto.getFileUrlList())){
            List<TicketMainTextFile> ticketMainTextFiles = new ArrayList<>();
            ticketDto.getFileUrlList().forEach(fileUrl -> {
                TicketMainTextFile ticketMainTextFile = new TicketMainTextFile();
                ticketMainTextFile.setFileUrl(fileUrl);
                ticketMainTextFile.setTicketMainTextId(ticketMainText.getId());
                ticketMainTextFiles.add(ticketMainTextFile);
            });
            ticketMainTextFileMapper.insertBatch(ticketMainTextFiles);
        }
        return Result.success(ticket);
    }


    /**
     * [用户回复工单]
     *
     * @param ticketDto 回复数据
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/29
     **/
    @Override
    @Transactional
    public Result replyTicket(TicketMainTextDto ticketDto) {
        Ticket ticket = ticketMapper.selectById(ticketDto.getTicketId());
        if (ticket==null){
            return Result.fail("工单不存在");
        }
        if (!StrUtil.equals(ticket.getUserId(),SecurityUtils.getStrUserId())){
            return Result.fail("您没有权限回复此工单");
        }
        if(TicketStatus.CLOSED.equals(ticket.getStatus())){
            return Result.fail("工单已关闭");
        }
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        TicketMainText ticketMainText = new TicketMainText();
        ticketMainText.setTicketId(ticket.getId());
        ticketMainText.setTicketMainText(ticketDto.getTicketMainText());
        ticketMainText.setUserId(SecurityUtils.getStrUserId());
        ticketMainText.setUserName(sysUser.getNickName());
        ticketMainTextMapper.insert(ticketMainText);
        //如果不为空新增文件
        if (!CollectionUtils.isEmpty(ticketDto.getFileUrlList())){
            List<TicketMainTextFile> ticketMainTextFiles = new ArrayList<>();
            ticketDto.getFileUrlList().forEach(fileUrl -> {
                TicketMainTextFile ticketMainTextFile = new TicketMainTextFile();
                ticketMainTextFile.setFileUrl(fileUrl);
                ticketMainTextFile.setTicketMainTextId(ticketMainText.getId());
                ticketMainTextFiles.add(ticketMainTextFile);
            });
            ticketMainTextFileMapper.insertBatch(ticketMainTextFiles);
        }
        ticketMapper.updateStatusById(ticketDto.getTicketId(),TicketStatus.WAITING_SERVICE_REPLY);
        return Result.success("回复工单成功");
    }

    /**
     * [用户关闭工单]
     *
     * @param id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/10
     **/
    @Override
    @Transactional
    public Result closeTicket(String id) {
        Ticket ticket = ticketMapper.selectById(id);
        if (ticket==null){
            return Result.fail("工单不存在");
        }
        if (!StrUtil.equals(ticket.getUserId(),SecurityUtils.getStrUserId())){
            return Result.fail("您没有权限关闭此工单");
        }
        if(TicketStatus.CLOSED.equals(ticket.getStatus())){
            return Result.fail("工单已关闭");
        }
        ticketMapper.updateStatusById(id,TicketStatus.CLOSED);
        return Result.success("工单关闭成功");
    }


    /**
     * [查询是否有需要回复的工单]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/10
     **/
    @Override
    public Result getNeedUserReply() {
        Integer needUserReply = ticketMapper.getNeedUserReply(SecurityUtils.getStrUserId());
        if (ObjectUtils.isEmpty(needUserReply)||needUserReply==0){
            return Result.success(false);
        }
        return Result.success(needUserReply);
    }


    /**
     * [客服回复工单]
     *
     * @param ticketDto 回复信息
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/11
     **/
    @Override
    public Result serviceReplyTicket(TicketMainTextDto ticketDto) {
        Ticket ticket = ticketMapper.selectById(ticketDto.getTicketId());
        if (ticket==null){
            return Result.fail("工单不存在");
        }
        if(TicketStatus.CLOSED.equals(ticket.getStatus())){
            return Result.fail("工单已关闭");
        }
        SysUser sysUser = sysUserMapper.selectUserById(SecurityUtils.getUserId());
        TicketMainText ticketMainText = new TicketMainText();
        ticketMainText.setTicketId(ticket.getId());
        ticketMainText.setTicketMainText(ticketDto.getTicketMainText());
        ticketMainText.setUserId(SecurityUtils.getStrUserId());
        ticketMainText.setUserName(sysUser.getNickName());
        ticketMainTextMapper.insert(ticketMainText);
        //如果不为空新增文件
        if (!CollectionUtils.isEmpty(ticketDto.getFileUrlList())){
            List<TicketMainTextFile> ticketMainTextFiles = new ArrayList<>();
            ticketDto.getFileUrlList().forEach(fileUrl -> {
                TicketMainTextFile ticketMainTextFile = new TicketMainTextFile();
                ticketMainTextFile.setFileUrl(fileUrl);
                ticketMainTextFile.setTicketMainTextId(ticketMainText.getId());
                ticketMainTextFiles.add(ticketMainTextFile);
            });
            ticketMainTextFileMapper.insertBatch(ticketMainTextFiles);
        }
        ticketMapper.updateStatusById(ticketDto.getTicketId(),TicketStatus.WAITING_USER_REPLY);
        return Result.success("回复工单成功");
    }

    /**
     * [查询是否有需要客服回复的工单]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/11
     **/
    @Override
    public Result getNeedServiceReply() {
        Integer needUserReply = ticketMapper.getNeedServiceReply();
        if (ObjectUtils.isEmpty(needUserReply)||needUserReply==0){
            return Result.success(false);
        }
        return Result.success(needUserReply);
    }

    /**
     * [用户查询工单]
     *
     * @param ticketDto 用户查询工单
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/11
     **/
    @Override
    public Result getUserTicketList(TicketDto ticketDto) {
        return null;
    }
}
