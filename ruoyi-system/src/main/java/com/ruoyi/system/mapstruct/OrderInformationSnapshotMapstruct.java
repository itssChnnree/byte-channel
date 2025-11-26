package com.ruoyi.system.mapstruct;


import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

import com.ruoyi.system.domain.entity.OrderInformationSnapshot;
import com.ruoyi.system.domain.dto.OrderInformationSnapshotDto;
import com.ruoyi.system.domain.vo.OrderInformationSnapshotVo;

/**
 * 订单快照(OrderInformationSnapshot)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-22 15:11:23
 */
@Mapper(componentModel = "spring")
public interface OrderInformationSnapshotMapstruct {
    OrderInformationSnapshotMapstruct INSTANCE = Mappers.getMapper(OrderInformationSnapshotMapstruct.class);

    /**
     * Entity to DTO
     *
     * @param orderInformationSnapshot
     * @return OrderInformationSnapshotDTO
     * @author chenxiangyue 2025-11-22 15:11:23
     **/
    OrderInformationSnapshotDto change2Dto(OrderInformationSnapshot orderInformationSnapshot);

    /**
     * DTO to Entity
     *
     * @param orderInformationSnapshotDto
     * @return OrderInformationSnapshot
     * @author chenxiangyue 2025-11-22 15:11:23
     **/
    OrderInformationSnapshot changeDto2(OrderInformationSnapshotDto orderInformationSnapshotDto);

    /**
     * DTO to VO
     *
     * @param orderInformationSnapshotDto
     * @return OrderInformationSnapshotVO
     * @author chenxiangyue 2025-11-22 15:11:23
     **/
    OrderInformationSnapshotVo changeDto2Vo(OrderInformationSnapshotDto orderInformationSnapshotDto);

    /**
     * vo to dto
     *
     * @param orderInformationSnapshotVo
     * @return OrderInformationSnapshotDTO
     * @author chenxiangyue 2025-11-22 15:11:23
     **/
    OrderInformationSnapshotDto changeVo2Dto(OrderInformationSnapshotVo orderInformationSnapshotVo);

    /**
     * Entity to VO
     *
     * @param orderInformationSnapshot
     * @return OrderInformationSnapshotVO
     * @author chenxiangyue 2025-11-22 15:11:23
     **/
    OrderInformationSnapshotVo change2Vo(OrderInformationSnapshot orderInformationSnapshot);

}
