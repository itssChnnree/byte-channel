package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.vo.OrderQuoteVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.OrderQuote;

/**
 * 订单报价表(OrderQuote
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-27 00:11:14
 */
@Mapper
@Repository
public interface OrderQuoteMapper extends BaseMapper<OrderQuote> {


    /**
     * [通过订单id查询报价详情]
     * @author 陈湘岳 2026/3/2
     * @param orderId 订单id
     * @return com.ruoyi.system.domain.vo.OrderQuoteVo
     **/
    OrderQuoteVo selectOrderQuoteVoByOrderId(@Param("orderId") String orderId);

}
