package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.WalletBalanceDetail;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
/**
 * 钱包余额明细表(WalletBalanceDetail)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:37
 */
@Mapper
@Repository
public interface WalletBalanceDetailMapper extends BaseMapper<WalletBalanceDetail> {

}
