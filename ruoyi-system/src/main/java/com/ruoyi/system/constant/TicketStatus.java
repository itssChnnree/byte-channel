package com.ruoyi.system.constant;

/**
 * @author a1152
 */
public interface TicketStatus {


    //新建
     String NEW = "新建";

    //关闭
     String CLOSED = "关闭";

    //等待用户回复
     String WAITING_USER_REPLY = "等待用户回复";

    //等待客服回复
     String WAITING_SERVICE_REPLY = "等待客服回复";

    //售前工单
     String PRE_SALE = "售前工单";

    //售后工单
     String AFTER_SALE = "售后工单";

    //其他工单
     String OTHER = "其他工单";

    //报价已接受
     String QUOTE_ACCEPTED = "报价已接受";

    //报价未处理
     String QUOTE_NOT_PROCESSED = "报价待处理";

     //报价已过期
     String QUOTE_EXPIRED = "报价已过期";

     //报价待付款
     String QUOTE_WAITING_PAYMENT = "报价待付款";

     //待分配资源
     String WAITING_RESOURCE_ALLOCATION = "待分配资源";

    //报价已拒绝
     String QUOTE_REJECTED = "报价已拒绝";
}
