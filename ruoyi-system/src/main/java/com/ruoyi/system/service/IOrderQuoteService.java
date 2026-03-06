package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.OrderQuoteDto;
import com.ruoyi.system.domain.dto.TicketMainTextQuoteDto;
import com.ruoyi.system.domain.entity.OrderQuote;
import com.ruoyi.system.domain.vo.OrderQuoteVo;
import com.ruoyi.system.http.Result;


/**
 * 订单报价表(OrderQuote)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-27 00:11:14
 */
public interface IOrderQuoteService {


    /**
     * [取消报价订单]
     * @author 陈湘岳 2026/3/1
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result cancelQuoteOrder(String orderId);

    /**
     * [报价订单支付]
     * @author 陈湘岳 2026/3/1
     * @param orderId 订单id
     * @param isBalance 是否余额支付
     * @return com.ruoyi.system.http.Result
     **/
    Result quoteOrderIsPay(String orderId, Boolean isBalance);

    /**
     * [查询订单详情页-报价处理记录信息]
     * @author 陈湘岳 2026/3/1
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result getQuoteOrderInfo(String orderId);

    /**
     * 创建报价订单
     *
     * @param ticketMainTextQuoteDto
     * @return
     */
    Result createOrderQuote(TicketMainTextQuoteDto ticketMainTextQuoteDto);
}
