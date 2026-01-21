package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.PromoRecordsDto;
import com.ruoyi.system.http.Result;

/**
 * 推广记录表(PromoRecords)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:20
 */
public interface IPromoRecordsService{


    /**
     * [获取已返现金额]
     * @author 陈湘岳 2026/1/5
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result getReturnCash();

    /**
     * [获取已成功邀请人数]
     * @author 陈湘岳 2026/1/5
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result getReturnPeopleNum();

    /**
     * [查询推广记录]
     * @author 陈湘岳 2026/1/5
     * @param promoRecordsDto 查询入参
     * @return com.ruoyi.system.http.Result
     **/
    Result getRecords(PromoRecordsDto promoRecordsDto);
}
