package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.VendorInformationDto;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.domain.entity.VendorInformation;
import com.ruoyi.system.http.Result;

/**
 * 厂商信息表(VendorInformation)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:34
 */
public interface IVendorInformationService{


    /**
     * [查询厂商信息]
     * @author 陈湘岳 2025/9/8
     * @param vendorInformationDto
     * @return com.ruoyi.system.http.Result
     **/
    Result queryList(VendorInformationDto vendorInformationDto);


    /**
     * [新增厂商]
     * @author 陈湘岳 2025/9/8
     * @param vendorInformation
     * @return com.ruoyi.system.http.Result
     **/
    Result save(VendorInformation vendorInformation);


    /**
     * [删除云服务商]
     * @author 陈湘岳 2025/9/8
     * @param id 云服务商id
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteById(String id);


    /**
     * [更新云服务商数据]
     * @author 陈湘岳 2025/9/8
     * @param vendorInformation 入参
     * @return com.ruoyi.system.http.Result
     **/
    Result updateById(VendorInformation vendorInformation);
}
