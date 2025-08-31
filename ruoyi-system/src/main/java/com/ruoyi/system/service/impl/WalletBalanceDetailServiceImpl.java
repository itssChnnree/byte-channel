package com.ruoyi.system.service.impl;


import com.github.pagehelper.PageHelper;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.domain.entity.WalletBalanceDetail;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.WalletBalanceDetailMapper;
import com.ruoyi.system.service.IWalletBalanceDetailService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 钱包余额明细表(WalletBalanceDetail)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:37
 */
@Service("walletBalanceDetailService")
public class WalletBalanceDetailServiceImpl  implements IWalletBalanceDetailService {

    @Resource
    private WalletBalanceDetailMapper walletBalanceDetailMapper;


    /**
     * [分页查询余额明细]
     *
     * @param pageBase 分页参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/29
     **/
    @Override
    public Result page(PageBase pageBase) {
        PageHelper.startPage(pageBase);
        return Result.success(walletBalanceDetailMapper.pageQuery(SecurityUtils.getStrUserId()));
    }
}
