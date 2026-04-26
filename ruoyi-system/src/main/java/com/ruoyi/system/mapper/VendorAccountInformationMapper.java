package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.VendorAccountInformationDto;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.domain.vo.VendorAccountInformationVo;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 厂商账号信息表(VendorAccountInformation)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:28
 */
@Mapper
@Repository
public interface VendorAccountInformationMapper extends BaseMapper<VendorAccountInformation> {


    /**
     * [厂商账号信息详情查询]
     * @author 陈湘岳 2025/9/8
     * @param id
     * @return com.ruoyi.system.domain.vo.VendorAccountInformationVo
     **/
    VendorAccountInformationVo queryById(String id);



    /**
     * [厂商账号信息列表查询]
     * @author 陈湘岳 2025/9/8
     * @param vendorAccountInformationDto
     * @return com.ruoyi.system.domain.vo.VendorAccountInformationVo
     **/
    List<VendorAccountInformationVo> queryList(VendorAccountInformationDto vendorAccountInformationDto);

    /**
     * [查询厂商账号总数]
     * @author 陈湘岳 2026/4/6
     * @return java.lang.Long
     **/
    Long countTotal();
}
