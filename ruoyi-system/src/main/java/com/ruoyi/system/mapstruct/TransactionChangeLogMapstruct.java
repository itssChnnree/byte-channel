package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.TransactionChangeLog;
import com.ruoyi.system.domain.dto.TransactionChangeLogDto;
import com.ruoyi.system.domain.vo.TransactionChangeLogVo;

/**
 * 流水线变更表(TransactionChangeLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:47:48
 */
@Mapper(componentModel = "spring")
public interface TransactionChangeLogMapstruct {
    TransactionChangeLogMapstruct INSTANCE = Mappers.getMapper(TransactionChangeLogMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param transactionChangeLog
     * @return TransactionChangeLogDTO
     * @author chenxiangyue 2026-02-23 17:47:48
     **/
    TransactionChangeLogDto change2Dto(TransactionChangeLog transactionChangeLog);

    /**
     * DTO to Entity
     *
     * @param transactionChangeLogDto
     * @return TransactionChangeLog
     * @author chenxiangyue 2026-02-23 17:47:48
     **/
    TransactionChangeLog changeDto2(TransactionChangeLogDto transactionChangeLogDto);

    /**
     * DTO to VO
     *
     * @param transactionChangeLogDto
     * @return TransactionChangeLogVO
     * @author chenxiangyue 2026-02-23 17:47:48
     **/
    TransactionChangeLogVo changeDto2Vo(TransactionChangeLogDto transactionChangeLogDto);

    /**
     * vo to dto
     *
     * @param transactionChangeLogVo
     * @return TransactionChangeLogDTO
     * @author chenxiangyue 2026-02-23 17:47:48
     **/
    TransactionChangeLogDto changeVo2Dto(TransactionChangeLogVo transactionChangeLogVo);

    /**
     * Entity to VO
     *
     * @param transactionChangeLog
     * @return TransactionChangeLogVO
     * @author chenxiangyue 2026-02-23 17:47:48
     **/
    TransactionChangeLogVo change2Vo(TransactionChangeLog transactionChangeLog);

}
