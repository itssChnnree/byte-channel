package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.vo.TicketMainTextDetailVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.TicketMainText;

import java.util.List;

/**
 * 工单正文表(TicketMainText)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:36
 */
@Mapper
@Repository
public interface TicketMainTextMapper extends BaseMapper<TicketMainText> {


    /**
     * [通过工单id查询工单正文详情]
     * @author 陈湘岳 2025/10/13
     * @param ticketId 工单id
     * @return java.util.List<com.ruoyi.system.domain.vo.TicketMainTextDetailVo>
     **/
    List<TicketMainTextDetailVo> findByTicketId(@Param("ticketId") String ticketId);
}
