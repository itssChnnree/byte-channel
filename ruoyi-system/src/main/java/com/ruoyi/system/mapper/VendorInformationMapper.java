package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.VendorInformationDto;
import com.ruoyi.system.domain.entity.VendorInformation;
import com.ruoyi.system.domain.vo.VendorInformationVo;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 厂商信息表(VendorInformation)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:34
 */
@Mapper
@Repository
public interface VendorInformationMapper extends BaseMapper<VendorInformation> {

    /**
     * [查询厂商信息集合]
     * @author 陈湘岳 2025/9/8
     * @param vendorInformationDto 厂商信息入参
     * @return java.util.List<com.ruoyi.system.domain.vo.VendorInformationVo>
     **/
    List<VendorInformationVo> queryList(VendorInformationDto vendorInformationDto);

    /**
     * [查询厂商下账号数量]
     * @author 陈湘岳 2025/9/8
     * @param vendorId 厂商id
     * @return java.lang.Integer
     **/
    Integer queryCountVendorAccountNum(String vendorId);

    /**
     * [查询厂商总数]
     * @author 陈湘岳 2026/4/6
     * @return java.lang.Long
     **/
    Long countTotal();

}
