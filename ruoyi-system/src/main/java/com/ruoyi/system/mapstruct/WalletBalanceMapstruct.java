package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.entity.WalletBalance;
import com.ruoyi.system.domain.dto.WalletBalanceDto;
import com.ruoyi.system.domain.vo.WalletBalanceVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 钱包余额表(WalletBalance)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:32
 */
@Mapper(componentModel = "spring")
public interface WalletBalanceMapstruct {
    WalletBalanceMapstruct INSTANCE = Mappers.getMapper(WalletBalanceMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param walletBalance
     * @return WalletBalanceDTO
     * @author chenxiangyue 2025-07-20 23:24:32
     **/
    WalletBalanceDto change2Dto(WalletBalance walletBalance);
    
    /**
     * DTO ת Entity
     *
     * @param walletBalanceDto
     * @return WalletBalance
     * @author chenxiangyue 2025-07-20 23:24:32
     **/
    WalletBalance changeDto2(WalletBalanceDto walletBalanceDto);
    
    /**
     * DTO ת VO
     *
     * @param walletBalanceDto
     * @return WalletBalanceVO
     * @author chenxiangyue 2025-07-20 23:24:32
     **/
    WalletBalanceVo changeDto2Vo(WalletBalanceDto walletBalanceDto);
    
    /**
     * VO ת DTO
     *
     * @param walletBalanceVo
     * @return WalletBalanceDTO
     * @author chenxiangyue 2025-07-20 23:24:32
     **/
    WalletBalanceDto changeVo2Dto(WalletBalanceVo walletBalanceVo);
    
    /**
     * Entity ת VO
     *
     * @param walletBalance
     * @return WalletBalanceVO
     * @author chenxiangyue 2025-07-20 23:24:32
     **/
    WalletBalanceVo change2Vo(WalletBalance walletBalance);
    
}
