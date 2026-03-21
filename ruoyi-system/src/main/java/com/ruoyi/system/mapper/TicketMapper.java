package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.TicketDto;
import com.ruoyi.system.domain.vo.TicketMainTextDetailVo;
import com.ruoyi.system.domain.vo.TicketVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.Ticket;

import java.util.Date;
import java.util.List;

/**
 * 工单主表(Ticket)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:35
 */
@Mapper
@Repository
public interface TicketMapper extends BaseMapper<Ticket> {


    /**
     * [根据id修改工单状态]
     * @author 陈湘岳 2025/10/10
     * @param id id
     * @param status 工单状态
     * @return int
     **/
    int updateStatusById(@Param("id") String id, @Param("status")String status);

    /**
     * [根据用户id工单状态为等待用户回复的工单]
     * @author 陈湘岳 2025/10/10
     * @param userId 用户id
     * @return int
     **/
    Integer getNeedUserReply(@Param("userId")String userId);

    /**
     * [根据用户id工单状态为等待用户回复的工单]
     * @author 陈湘岳 2025/10/10
     * @return int
     **/
    Integer getNeedServiceReply();

    /**
     * [根据条件分页查询]
     * @author 陈湘岳 2025/10/13
     * @param ticketDto 查询条件
     * @return java.util.List<com.ruoyi.system.domain.vo.TicketVo>
     **/
    List<TicketVo> selectList(@Param("dto") TicketDto ticketDto,@Param("userId")String userId,@Param("orderType")String orderType);


    /**
     * [根据报价单id查询工单]
     * @author 陈湘岳 2025/10/13
     * @param quoteId 报价单id
     * @return com.ruoyi.system.domain.entity.Ticket
     **/
    Ticket selectByQuoteId(@Param("quoteId")String quoteId);

    /**
     * [批量关闭超时工单]
     * 将状态不等于关闭，且updateTime在指定日期之前的工单状态设置为关闭
     * @author 陈湘岳 2026/3/18
     * @param closedStatus 关闭状态
     * @param beforeDate 截止日期
     * @return int 关闭的工单数量
     **/
    int closeTimeoutTickets(@Param("closedStatus")String closedStatus, @Param("beforeDate")Date beforeDate);

}
