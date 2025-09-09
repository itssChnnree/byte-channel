package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.VendorAccountInformationDto;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.domain.vo.VendorAccountInformationVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 厂商账号信息表(VendorAccountInformation)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:27
 */
@Mapper(componentModel = "spring")
public interface VendorAccountInformationMapstruct {
    VendorAccountInformationMapstruct INSTANCE = Mappers.getMapper(VendorAccountInformationMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param vendorAccountInformation
     * @return VendorAccountInformationDTO
     * @author chenxiangyue 2025-09-08 16:47:27
     **/
    VendorAccountInformationDto change2Dto(VendorAccountInformation vendorAccountInformation);

    /**
     * DTO ת Entity
     *
     * @param vendorAccountInformationDto
     * @return VendorAccountInformation
     * @author chenxiangyue 2025-09-08 16:47:27
     **/
    VendorAccountInformation changeDto2(VendorAccountInformationDto vendorAccountInformationDto);

    /**
     * DTO ת VO
     *
     * @param vendorAccountInformationDto
     * @return VendorAccountInformationVO
     * @author chenxiangyue 2025-09-08 16:47:27
     **/
    VendorAccountInformationVo changeDto2Vo(VendorAccountInformationDto vendorAccountInformationDto);

    /**
     * VO ת DTO
     *
     * @param vendorAccountInformationVo
     * @return VendorAccountInformationDTO
     * @author chenxiangyue 2025-09-08 16:47:27
     **/
    VendorAccountInformationDto changeVo2Dto(VendorAccountInformationVo vendorAccountInformationVo);

    /**
     * Entity ת VO
     *
     * @param vendorAccountInformation
     * @return VendorAccountInformationVO
     * @author chenxiangyue 2025-09-08 16:47:27
     **/
    VendorAccountInformationVo change2Vo(VendorAccountInformation vendorAccountInformation);

}
