package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.TicketDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.Ticket;

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
}
