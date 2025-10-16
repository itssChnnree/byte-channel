package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.TicketStatus;
import com.ruoyi.system.domain.dto.TicketDto;
import com.ruoyi.system.domain.dto.TicketMainTextDto;
import com.ruoyi.system.domain.entity.Ticket;
import com.ruoyi.system.domain.entity.TicketMainText;
import com.ruoyi.system.domain.entity.TicketMainTextFile;
import com.ruoyi.system.domain.entity.TicketMainTextQuote;
import com.ruoyi.system.domain.vo.*;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.*;
import com.ruoyi.system.mapstruct.TicketMapstruct;
import com.ruoyi.system.service.ITicketService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * 工单主表(Ticket)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:35
 */
@Slf4j
@Service("ticketService")
public class TicketServiceImpl  implements ITicketService {

    @Resource
    private TicketMapstruct ticketMapstruct;

    @Resource
    private TicketMapper ticketMapper;

    @Resource
    private SysUserMapper  sysUserMapper;

    @Resource
    private TicketMainTextQuoteMapper ticketMainTextQuoteMapper;


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
            return Result.success(0);
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
        //判断是否为报价单
        if (!ObjectUtils.isEmpty(ticketDto.getQuote())){
            TicketMainTextQuote ticketMainTextQuote = new TicketMainTextQuote();
            ticketMainTextQuote.setTicketMainTextId(ticketMainText.getId());
            ticketMainTextQuote.setQuote(ticketDto.getQuote());
            ticketMainTextQuote.setStatus(TicketStatus.QUOTE_NOT_PROCESSED);
            ticketMainTextQuoteMapper.insert(ticketMainTextQuote);
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
            return Result.success(0);
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
        PageUtils.startPage(ticketDto);
        List<TicketVo> ticketVos = ticketMapper.selectList(ticketDto,SecurityUtils.getStrUserId(),"desc");
        PageUtils.clearPage();
        return Result.success(new PageInfo<>(ticketVos));
    }

    /**
     * [客服查询工单]
     *
     * @param ticketDto 查询参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/13
     **/
    @Override
    public Result getServiceTicketList(TicketDto ticketDto) {
        String orderType = "desc";
        if (!CollectionUtils.isEmpty(ticketDto.getStatusList())){
            if (ticketDto.getStatusList().contains(TicketStatus.WAITING_SERVICE_REPLY)
                    ||ticketDto.getStatusList().contains(TicketStatus.NEW))
                orderType = "asc";
        }
        PageUtils.startPage(ticketDto);
        List<TicketVo> ticketVos = ticketMapper.selectList(ticketDto,null,orderType);
        PageUtils.clearPage();
        return Result.success(new PageInfo<>(ticketVos));
    }


    /**
     * [查询工单详情]
     *
     * @param id 工单id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/10/13
     **/
    @Override
    public Result getTicketDetails(String id,Boolean hasPermi,int pageNum,int pageSize) {
        Ticket ticket = ticketMapper.selectById(id);
        if (!hasPermi&&!StrUtil.equals(ticket.getUserId(),SecurityUtils.getStrUserId())){
            return Result.fail("您没有权限查看此工单");
        }
        TicketDetailVo ticketDetailVo = ticketMapstruct.change2DetailVo(ticket);
        //查询工单正文
        PageHelper.startPage(pageNum,pageSize);
        List<TicketMainTextDetailVo> byTicketId = ticketMainTextMapper.findByTicketId(id);
        PageUtils.clearPage();
        if (CollectionUtils.isEmpty(byTicketId)){
            return Result.success(ticketDetailVo);
        }
        String strUserId = SecurityUtils.getStrUserId();
        //提取出工单正文id
        List<String> collect = byTicketId.stream().map(TicketMainTextDetailVo::getId).collect(Collectors.toList());
        //为工单正文设置文件
        CompletableFuture<Void> setFileCompletableFuture = CompletableFuture.runAsync(() -> {
            setFile(collect, byTicketId,strUserId);
        });

        //设置报价
        CompletableFuture<Void> setQuoteCompletableFuture = CompletableFuture.runAsync(() -> {
            setQuote(collect, byTicketId);
        });
        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.allOf(setFileCompletableFuture, setQuoteCompletableFuture);
        try {
            voidCompletableFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
            log.error("设置文件或报价失败{}", e.getMessage());
            return Result.fail("查询工单详情失败");
        }
        ticketDetailVo.setTicketMainTextDetailVos(byTicketId);
        return Result.success(ticketDetailVo);
    }

    private void setFile(List<String> collect, List<TicketMainTextDetailVo> byTicketId,String strUserId) {
        //获取工单正文文件
        List<TicketMainTextFileVo> byTicketMainTextId = ticketMainTextFileMapper.findByTicketMainTextId(collect);
        //键为工单正文id  值为工单正文下的文件集合
        Map<String,List<TicketMainTextFileVo>> map = new HashMap<>();
        //循环工单正文文件集合,生成  工单正文id:工单正文下的文件集合
        byTicketMainTextId.forEach(ticketMainTextFileVo->{
            if (map.containsKey(ticketMainTextFileVo.getTicketMainTextId())){
                map.get(ticketMainTextFileVo.getTicketMainTextId()).add(ticketMainTextFileVo);
            }else {
                List<TicketMainTextFileVo> list = new ArrayList<>();
                list.add(ticketMainTextFileVo);
                map.put(ticketMainTextFileVo.getTicketMainTextId(),list);
            }
        });
        //循环工单正文集合,为工单正文设置工单正文下的文件集合
        byTicketId.forEach(ticketMainTextDetailVo -> {
            if (map.containsKey(ticketMainTextDetailVo.getId())){
                ticketMainTextDetailVo.setTicketMainTextFileVos(map.get(ticketMainTextDetailVo.getId()));
            }
            ticketMainTextDetailVo.setIsMe(StrUtil.equals(strUserId, ticketMainTextDetailVo.getUserId()));
        });
    }

    //设置报价
    private void setQuote(List<String> collect, List<TicketMainTextDetailVo> byTicketId) {
        //获取工单正文文件
        List<TicketMainTextQuoteVo> byTicketMainTextId = ticketMainTextQuoteMapper.findByTicketMainTextId(collect);
        //键为工单正文id  值为工单正文下的文件集合
        Map<String,TicketMainTextQuoteVo> map = new HashMap<>();
        //循环工单正文文件集合,生成  工单正文id:工单正文下的文件集合
        byTicketMainTextId.forEach(ticketMainTextQuoteVo->{
            if (!map.containsKey(ticketMainTextQuoteVo.getTicketMainTextId())){
                map.put(ticketMainTextQuoteVo.getTicketMainTextId(),ticketMainTextQuoteVo);
            }
        });
        //循环工单正文集合,为工单正文设置工单正文下的文件集合
        byTicketId.forEach(ticketMainTextDetailVo -> {
            if (map.containsKey(ticketMainTextDetailVo.getId())){
                ticketMainTextDetailVo.setTicketMainTextQuoteVo(map.get(ticketMainTextDetailVo.getId()));
            }
        });
    }
}
