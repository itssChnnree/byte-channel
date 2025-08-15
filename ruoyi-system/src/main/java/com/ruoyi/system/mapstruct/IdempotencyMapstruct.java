package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.IdempotencyDto;
import com.ruoyi.system.domain.vo.IdempotencyVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import com.ruoyi.system.domain.entity.Idempotency;

/**
 * 幂等性控制表(Idempotency)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-15 14:25:31
 */
@Mapper
public interface IdempotencyMapstruct {
    IdempotencyMapstruct INSTANCE = Mappers.getMapper(IdempotencyMapstruct.class);
    
    /**
     * EntityתDTO
     *
     * @param idempotency
     * @return IdempotencyDTO
     * @author chenxiangyue 2025-08-15 14:25:31
     **/
    IdempotencyDto change2Dto(Idempotency idempotency);
    
    /**
     * DTO ת Entity
     *
     * @param idempotencyDto
     * @return Idempotency
     * @author chenxiangyue 2025-08-15 14:25:31
     **/
    Idempotency changeDto2(IdempotencyDto idempotencyDto);
    
    /**
     * DTO ת VO
     *
     * @param idempotencyDto
     * @return IdempotencyVO
     * @author chenxiangyue 2025-08-15 14:25:31
     **/
    IdempotencyVo changeDto2Vo(IdempotencyDto idempotencyDto);
    
    /**
     * VO ת DTO
     *
     * @param idempotencyVo
     * @return IdempotencyDTO
     * @author chenxiangyue 2025-08-15 14:25:31
     **/
    IdempotencyDto changeVo2Dto(IdempotencyVo idempotencyVo);
    
    /**
     * Entity ת VO
     *
     * @param idempotency
     * @return IdempotencyVO
     * @author chenxiangyue 2025-08-15 14:25:31
     **/
    IdempotencyVo change2Vo(Idempotency idempotency);
    
}
