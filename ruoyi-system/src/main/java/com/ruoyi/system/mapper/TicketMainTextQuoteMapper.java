package com.ruoyi.system.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.vo.TicketMainTextQuoteVo;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.TicketMainTextQuote;

import java.util.List;

/**
 * 工单正文报价表(TicketMainTextQuote)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-14 22:36:23
 */
@Mapper
@Repository
public interface TicketMainTextQuoteMapper extends BaseMapper<TicketMainTextQuote> {

    /**
     * [查询工单正文报价]
     * @author 陈湘岳 2025/10/15
     * @param collect 工单正文id集合
     * @return java.util.List<com.ruoyi.system.domain.vo.TicketMainTextQuoteVo>
     **/
    List<TicketMainTextQuoteVo> findByTicketMainTextId(List<String> collect);
}
