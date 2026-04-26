package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.OrderQuoteDto;
import com.ruoyi.system.domain.dto.ProcessQuoteDto;
import com.ruoyi.system.domain.dto.TicketMainTextQuoteDto;
import com.ruoyi.system.domain.dto.UpdateQuoteRecordDto;
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
    Result cancelQuoteOrder(String orderId,Boolean refoundToBalance);

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

    /**
     * [处理报价订单]
     * @author 陈湘岳 2026/4/6
     * @param processQuoteDto 处理参数
     * @return com.ruoyi.system.http.Result
     **/
    Result processQuoteOrder(ProcessQuoteDto processQuoteDto);

    /**
     * [修改报价处理记录]
     * @author 陈湘岳 2026/4/6
     * @param updateQuoteRecordDto 修改参数
     * @return com.ruoyi.system.http.Result
     **/
    Result updateQuoteRecord(UpdateQuoteRecordDto updateQuoteRecordDto);

    /**
     * [订单信息页-报价处理记录查询]
     * @author 陈湘岳 2026/4/15
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result getQuoteOrderRecord(String orderId);

    /**
     * [根据订单id修改报价单状态]
     * @author 陈湘岳 2026/4/18
     * @param orderId 订单id
     * @param status 状态
     * @return void
     **/
    void updateTicketMainTextQuote(String orderId,String status);
}
