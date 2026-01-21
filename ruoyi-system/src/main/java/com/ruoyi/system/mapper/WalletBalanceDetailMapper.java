package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.domain.entity.WalletBalanceDetail;
import com.ruoyi.system.domain.vo.WalletBalanceDetailVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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



    /**
     * [通过用户id查询前辈余额明细]
     * @author 陈湘岳 2025/8/2 9
     * @param userId 用户id
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ruoyi.system.domain.vo.WalletBalanceDetailVo>
     **/
    List<WalletBalanceDetailVo> pageQuery(@Param("userId")String userId);

}
