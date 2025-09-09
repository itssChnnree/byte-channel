package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.dto.VendorAccountInformationDto;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.http.Result;

/**
 * 厂商账号信息表(VendorAccountInformation)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:32
 */
public interface IVendorAccountInformationService{


    /**
     * [详情查询]
     * @author 陈湘岳 2025/9/8
     * @param id
     * @return com.ruoyi.system.http.Result
     **/
    Result queryById(String id);

    /**
     * [分页查询]
     * @author 陈湘岳 2025/9/8
     * @param vendorAccountInformationDto
     * @return com.ruoyi.system.http.Result
     **/
    Result queryList(VendorAccountInformationDto vendorAccountInformationDto);

    /**
     * [新增]
     * @author 陈湘岳 2025/9/8
     * @param vendorAccountInformation
     * @return com.ruoyi.system.http.Result
     **/
    Result save(VendorAccountInformation vendorAccountInformation);

    /**
     * [修改]
     * @author 陈湘岳 2025/9/8
     * @param vendorAccountInformation
     * @return com.ruoyi.system.http.Result
     **/
    Result updateById(VendorAccountInformation vendorAccountInformation);


    /**
     * [删除账号]
     * @author 陈湘岳 2025/9/8
     * @param id 删除账号id
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteById(String id);
}
