package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.entity.TicketMainTextOrder;
import com.ruoyi.system.domain.vo.OrderVo;
import com.ruoyi.system.domain.vo.TicketMainTextOrderVo;
import com.ruoyi.system.domain.vo.TicketMainTextQuoteVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 工单正文订单信息表(TicketMainTextOrder
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:52:30
 */
@Mapper
@Repository
public interface TicketMainTextOrderMapper extends BaseMapper<TicketMainTextOrder> {

    /**
     * [通过工单正文id查询关联的订单]
     * @author 陈湘岳 2026/2/23
     * @param collect 工单正文id集合
     * @return java.util.List<com.ruoyi.system.domain.vo.OrderVo>
     **/
    List<TicketMainTextOrderVo> findByTicketMainTextId(@Param("collect") List<String> collect);
}
