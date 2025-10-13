package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.TicketDto;
import com.ruoyi.system.domain.dto.TicketMainTextDto;
import com.ruoyi.system.http.Result;

import javax.validation.Valid;

/**
 * 工单主表(Ticket)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:35
 */
public interface ITicketService {


    /**
     * [用户提交工单]
     * @author 陈湘岳 2025/9/29
     * @param ticketDto 工单信息
     * @return com.ruoyi.system.http.Result
     **/
    Result insert(TicketDto ticketDto);


    /**
     * [用户回复工单]
     * @author 陈湘岳 2025/9/29
     * @param ticketDto 回复数据
     * @return com.ruoyi.system.http.Result
     **/
    Result replyTicket(TicketMainTextDto ticketDto);

    /**
     * [用户关闭工单]
     * @author 陈湘岳 2025/10/10
     * @param id
     * @return com.ruoyi.system.http.Result
     **/
    Result closeTicket(String id);

    /**
     * [查询是否有需要回复的工单]
     * @author 陈湘岳 2025/10/10
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result getNeedUserReply();


    /**
     * [客服回复工单]
     * @author 陈湘岳 2025/10/11
     * @param ticketDto 回复信息
     * @return com.ruoyi.system.http.Result
     **/
    Result serviceReplyTicket(@Valid TicketMainTextDto ticketDto);


    /**
     * [查询是否有需要客服回复的工单]
     * @author 陈湘岳 2025/10/11
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result getNeedServiceReply();

    /**
     * [用户查询工单]
     * @author 陈湘岳 2025/10/11
     * @param ticketDto 用户查询工单
     * @return com.ruoyi.system.http.Result
     **/
    Result getUserTicketList(TicketDto ticketDto);
}
