package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.VendorInformationDto;
import com.ruoyi.system.domain.entity.VendorInformation;
import com.ruoyi.system.domain.vo.VendorInformationVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 厂商信息表(VendorInformation)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:34
 */
@Mapper(componentModel = "spring")
public interface VendorInformationMapstruct {
    VendorInformationMapstruct INSTANCE = Mappers.getMapper(VendorInformationMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param vendorInformation
     * @return VendorInformationDTO
     * @author chenxiangyue 2025-09-08 16:47:34
     **/
    VendorInformationDto change2Dto(VendorInformation vendorInformation);

    /**
     * DTO ת Entity
     *
     * @param vendorInformationDto
     * @return VendorInformation
     * @author chenxiangyue 2025-09-08 16:47:34
     **/
    VendorInformation changeDto2(VendorInformationDto vendorInformationDto);

    /**
     * DTO ת VO
     *
     * @param vendorInformationDto
     * @return VendorInformationVO
     * @author chenxiangyue 2025-09-08 16:47:34
     **/
    VendorInformationVo changeDto2Vo(VendorInformationDto vendorInformationDto);

    /**
     * VO ת DTO
     *
     * @param vendorInformationVo
     * @return VendorInformationDTO
     * @author chenxiangyue 2025-09-08 16:47:34
     **/
    VendorInformationDto changeVo2Dto(VendorInformationVo vendorInformationVo);

    /**
     * Entity ת VO
     *
     * @param vendorInformation
     * @return VendorInformationVO
     * @author chenxiangyue 2025-09-08 16:47:34
     **/
    VendorInformationVo change2Vo(VendorInformation vendorInformation);

}
