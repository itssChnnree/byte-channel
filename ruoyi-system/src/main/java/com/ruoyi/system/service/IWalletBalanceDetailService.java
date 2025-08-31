package com.ruoyi.system.service;


import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.http.Result;

/**
 * 钱包余额明细表(WalletBalanceDetail)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:37
 */
public interface IWalletBalanceDetailService{


    /**
     * [分页查询余额明细]
     * @author 陈湘岳 2025/8/29
     * @param pageBase 分页参数
     * @return com.ruoyi.system.http.Result
     **/
    Result page(PageBase pageBase);
}
