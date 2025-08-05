package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.WalletBalanceDetail;
import com.ruoyi.system.domain.dto.WalletBalanceDetailDto;
import com.ruoyi.system.domain.vo.WalletBalanceDetailVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;


/**
 * 钱包余额明细表(WalletBalanceDetail)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:37
 */
@Mapper(componentModel = "spring")
public interface WalletBalanceDetailMapstruct {
    WalletBalanceDetailMapstruct INSTANCE = Mappers.getMapper(WalletBalanceDetailMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param walletBalanceDetail
     * @return WalletBalanceDetailDTO
     * @author chenxiangyue 2025-07-20 23:24:37
     **/
    WalletBalanceDetailDto change2Dto(WalletBalanceDetail walletBalanceDetail);
    
    /**
     * DTO ת Entity
     *
     * @param walletBalanceDetailDto
     * @return WalletBalanceDetail
     * @author chenxiangyue 2025-07-20 23:24:37
     **/
    WalletBalanceDetail changeDto2(WalletBalanceDetailDto walletBalanceDetailDto);
    
    /**
     * DTO ת VO
     *
     * @param walletBalanceDetailDto
     * @return WalletBalanceDetailVO
     * @author chenxiangyue 2025-07-20 23:24:37
     **/
    WalletBalanceDetailVo changeDto2Vo(WalletBalanceDetailDto walletBalanceDetailDto);
    
    /**
     * VO ת DTO
     *
     * @param walletBalanceDetailVo
     * @return WalletBalanceDetailDTO
     * @author chenxiangyue 2025-07-20 23:24:37
     **/
    WalletBalanceDetailDto changeVo2Dto(WalletBalanceDetailVo walletBalanceDetailVo);
    
    /**
     * Entity ת VO
     *
     * @param walletBalanceDetail
     * @return WalletBalanceDetailVO
     * @author chenxiangyue 2025-07-20 23:24:37
     **/
    WalletBalanceDetailVo change2Vo(WalletBalanceDetail walletBalanceDetail);
    
}
