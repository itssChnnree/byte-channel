package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.WalletBalance;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;

/**
 * 钱包余额表(WalletBalance)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:32
 */
@Mapper
@Repository
public interface WalletBalanceMapper extends BaseMapper<WalletBalance> {


    /**
     * [通过用户id查询余额]
     * @author 陈湘岳 2025/8/28
     * @param userId
     * @return java.math.BigDecimal
     **/
    BigDecimal findByUserId(@Param("userId") String userId);
}
