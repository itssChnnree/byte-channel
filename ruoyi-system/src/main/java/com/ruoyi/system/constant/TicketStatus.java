package com.ruoyi.system.constant;

/**
 * @author a1152
 */
public interface TicketStatus {


    //新建
    public static final String NEW = "新建";

    //关闭
    public static final String CLOSED = "关闭";

    //等待用户回复
    public static final String WAITING_USER_REPLY = "等待用户回复";

    //等待客服回复
    public static final String WAITING_SERVICE_REPLY = "等待客服回复";

    //售前工单
    public static final String PRE_SALE = "售前工单";

    //售后工单
    public static final String AFTER_SALE = "售后工单";

    //其他工单
    public static final String OTHER = "其他工单";

    //报价已接受
    public static final String QUOTE_ACCEPTED = "报价已接受";

    //报价未处理
    public static final String QUOTE_NOT_PROCESSED = "报价待处理";

    //报价已拒绝
    public static final String QUOTE_REJECTED = "报价已拒绝";
}
