package com.ruoyi.system.service.impl;



import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.dto.VendorAccountInformationDto;
import com.ruoyi.system.domain.entity.CommodityCategory;
import com.ruoyi.system.domain.entity.VendorAccountInformation;
import com.ruoyi.system.domain.entity.VendorInformation;
import com.ruoyi.system.domain.vo.CommodityVo;
import com.ruoyi.system.domain.vo.VendorAccountInformationVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapper.VendorAccountInformationMapper;
import com.ruoyi.system.mapper.VendorInformationMapper;
import com.ruoyi.system.mapstruct.VendorAccountInformationMapstruct;
import com.ruoyi.system.service.IVendorAccountInformationService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 厂商账号信息表(VendorAccountInformation)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-08 16:47:33
 */
@Service("vendorAccountInformationService")
public class VendorAccountInformationServiceImpl  implements IVendorAccountInformationService {


    @Resource
    private VendorAccountInformationMapper vendorAccountInformationMapper;

    @Resource
    private VendorAccountInformationMapstruct vendorAccountInformationMapstruct;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private VendorInformationMapper vendorInformationMapper;


    /**
     * [详情查询]
     *
     * @param id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result queryById(String id) {
        VendorAccountInformationVo vendorAccountInformationVo = vendorAccountInformationMapper.queryById(id);
        if (vendorAccountInformationVo == null){
            return Result.fail("未查询到云服务商账号信息");
        }
        return Result.success(vendorAccountInformationVo);
    }

    /**
     * [删除账号]
     *
     * @param id 需要删除的账号
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result deleteById(String id) {
        if (StrUtil.isEmpty(id)){
            return Result.fail("请选择要删除的账号");
        }else{
            Integer resourcesNum = serverResourcesMapper.haveServerResourcesByAccount(id);
            //筛选出不存在商品的类别
            if (ObjectUtil.isNotEmpty(resourcesNum)&& resourcesNum > 0){
                return Result.fail("所选账号下存在资源");
            }
            int delete = vendorAccountInformationMapper.deleteById(id);
            if (delete > 0){
                return Result.success("已删除该账号");
            }else {
                return Result.fail("删除失败");
            }
        }
    }

    /**
     * [分页查询]
     *
     * @param vendorAccountInformationDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result queryList(VendorAccountInformationDto vendorAccountInformationDto) {
        List<VendorAccountInformationVo> vendorAccountInformationVo = vendorAccountInformationMapper.queryList(vendorAccountInformationDto);
        return Result.success(vendorAccountInformationVo);
    }

    /**
     * [新增]
     *
     * @param vendorAccountInformation
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result save(VendorAccountInformation vendorAccountInformation) {
        VendorInformation vendorInformation = vendorInformationMapper.selectById(vendorAccountInformation.getVendorId());
        if (ObjectUtil.isEmpty(vendorInformation)){
            return Result.fail("所选厂商不存在");
        }
        int insert = vendorAccountInformationMapper.insert(vendorAccountInformation);
        if (insert > 0){
            return Result.success(vendorAccountInformation);
        }else {
            return Result.fail("添加失败");
        }
    }

    /**
     * [修改]
     *
     * @param vendorAccountInformation
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/8
     **/
    @Override
    public Result updateById(VendorAccountInformation vendorAccountInformation) {
        int i = vendorAccountInformationMapper.updateById(vendorAccountInformation);
        if (i > 0){
            return Result.success(vendorAccountInformation);
        }else {
            return Result.fail("修改失败");
        }
    }
}
