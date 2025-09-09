package com.ruoyi.system.service.impl;


import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.system.domain.dto.VendorInformationDto;
import com.ruoyi.system.domain.entity.CommodityCategory;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.domain.entity.VendorInformation;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.VendorInformationMapper;
import com.ruoyi.system.mapstruct.VendorInformationMapstruct;
import com.ruoyi.system.service.IVendorInformationService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 厂商信息表(VendorInformation)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:34
 */
@Service("vendorInformationService")
public class VendorInformationServiceImpl  implements IVendorInformationService {


    @Resource
    private VendorInformationMapper vendorInformationMapper;


    @Resource
    private VendorInformationMapstruct vendorInformationMapstruct;


    /**
     * [查询厂商信息]
     *
     * @param vendorInformationDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result queryList(VendorInformationDto vendorInformationDto) {
        return Result.success(vendorInformationMapper.queryList(vendorInformationDto));
    }

    /**
     * [新增厂商]
     *
     * @param vendorInformation
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result save(VendorInformation vendorInformation) {
        int insert = vendorInformationMapper.insert(vendorInformation);
        if (insert > 0){
            return Result.success(vendorInformation);
        }else {
            return Result.fail("添加失败");
        }
    }

    /**
     * [删除云服务商]
     *
     * @param id 云服务商id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result deleteById(String id) {
        Integer i = vendorInformationMapper.queryCountVendorAccountNum(id);
        if (ObjectUtil.isNotNull( i)&& i > 0){
            return Result.fail("该厂商下有账号，请先删除账号");
        }
        int delete = vendorInformationMapper.deleteById(id);
        if (delete > 0){
            return Result.success("删除成功");
        }else {
            return Result.fail("删除失败");
        }
    }

    /**
     * [更新云服务商数据]
     *
     * @param vendorInformation 入参
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result updateById(VendorInformation vendorInformation) {
        int i = vendorInformationMapper.updateById(vendorInformation);
        if (i > 0){
            return Result.success(vendorInformation);
        }else {
            return Result.fail("修改失败");
        }
    }
}
