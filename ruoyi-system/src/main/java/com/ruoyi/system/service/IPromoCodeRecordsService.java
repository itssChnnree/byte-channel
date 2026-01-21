package com.ruoyi.system.service;


import com.ruoyi.system.http.Result;

/**
 * 推广码记录表(PromoCodeRecords)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:13
 */
public interface IPromoCodeRecordsService{


    /**
     * [生成推广码]
     * @author 陈湘岳 2025/8/14
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result createPromoCodeRecords();


    /**
     * [获取推广码]
     * @author 陈湘岳 2025/12/29
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result getPromoCodeRecords();
}
