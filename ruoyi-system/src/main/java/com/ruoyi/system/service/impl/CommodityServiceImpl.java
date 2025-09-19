package com.ruoyi.system.service.impl;


import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.util.DefaultValueUtil;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.CommodityCategory;
import com.ruoyi.system.domain.dto.CommodityDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.vo.CommodityVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityCategoryMapper;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapstruct.CommodityMapstruct;
import com.ruoyi.system.service.ICommodityService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品表(Commodity)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:34
 */
@Service("commodityService")
public class CommodityServiceImpl  implements ICommodityService {

    @Resource
    private CommodityCategoryMapper commodityCategoryMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private CommodityMapstruct commodityMapstruct;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;


    /**
     * [创建商品]
     *
     * @param commodityDto 创建商品参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/7/29
     **/
    @Override
    public Result insert(CommodityDto commodityDto) {
        try {
            DefaultValueUtil.setDefaultData(commodityDto);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("默认值设置失败");
        }
        CommodityCategory commodityCategory = commodityCategoryMapper.selectById(commodityDto.getCategoryId());
        if (commodityCategory == null||commodityCategory.getIsDeleted()==1){
            return Result.fail("商品类别不存在");
        }
        Commodity commodity = commodityMapstruct.changeDto2(commodityDto);
        if (!CollectionUtils.isEmpty(commodityDto.getBusinessSuitableList())){
            String businessSuitable = StrUtil.join(",", commodityDto.getBusinessSuitableList());
            commodity.setBusinessSuitable(businessSuitable);
        }
        int insert = commodityMapper.insert(commodity);
        return insert > 0 ? Result.success(commodity) : Result.fail("添加失败");
    }

    /**
     * [修改商品]
     *
     * @param commodityDto 商品修改入参
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/1
     **/
    @Override
    public Result update(CommodityDto commodityDto) {
        Commodity commodity = commodityMapstruct.changeDto2(commodityDto);
        if (!StrUtil.isBlank(commodityDto.getCategoryId())){
            CommodityCategory commodityCategory = commodityCategoryMapper.selectById(commodityDto.getCategoryId());
            if (commodityCategory == null||commodityCategory.getIsDeleted()==1){
                return Result.fail("商品类别不存在");
            }
        }
        if (!CollectionUtils.isEmpty(commodityDto.getBusinessSuitableList())){
            String businessSuitable = StrUtil.join(",", commodityDto.getBusinessSuitableList());
            commodity.setBusinessSuitable(businessSuitable);
        }
        int i = commodityMapper.update(commodity);
        return i > 0 ? Result.success(commodity) : Result.fail("修改失败");
    }

    /**
     * [删除商品]
     *
     * @param listDto 删除商品id集合
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/1
     **/
    @Override
    public Result deleteByIds(ListDto listDto) {
        if (CollectionUtils.isEmpty(listDto.getIds())){
            return Result.fail("请选择要删除的商品");
        }else{
            List<String> strings = serverResourcesMapper.haveServerResources(listDto.getIds());
            //筛选出不存在商品的类别
            List<String> collect = listDto.getIds().stream().filter(id -> !strings.contains(id)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty( collect)){
                return Result.fail("所选类别下存在商品");
            }
            int delete = commodityMapper.deleteBatchIds(collect);
            if (delete > 0){
                return Result.success("已删除该商品");
            }else {
                return Result.fail("删除失败");
            }
        }
    }


    /**
     * [分页查询商品]
     *
     * @param commodityDto 查询商品参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/1
     **/
    @Override
    public Result page(CommodityDto commodityDto) {
        //        Page<CommodityCategory> page = new Page<>(listDto.getCurrentPage(), listDto.getPageSize());
        PageHelper.startPage(commodityDto);
        List<CommodityVo> commodityCategoryVoIPage = commodityMapper.queryPage(commodityDto);
        commodityCategoryVoIPage.forEach(commodityVo -> {
            List<String> split = StrUtil.split(commodityVo.getBusinessSuitable(), ",");
            commodityVo.setBusinessSuitableList(split);
        });
        return Result.success(new PageInfo<>(commodityCategoryVoIPage));
    }



    /**
     * [通过商品id查询商品信息]
     *
     * @param id 通过详情查询商品信息
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/1
     **/
    @Override
    public Result findById(String id) {
        CommodityVo commodityVo = commodityMapper.queryById(id);
        if (commodityVo == null){
            return Result.fail("未查询到商品信息");
        }
        if(!StrUtil.isEmpty(commodityVo.getBusinessSuitable())){
            List<String> split = StrUtil.split(commodityVo.getBusinessSuitable(), ",");
            commodityVo.setBusinessSuitableList(split);
        }
        return Result.success(commodityVo);
    }


    /**
     * [用户分页查询商品]
     *
     * @param commodityDto 查询参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/31
     **/
    @Override
    public Result userPage(CommodityDto commodityDto) {
        List<CommodityVo> commodityCategoryVoIPage = commodityMapper.userPage(commodityDto);
        commodityCategoryVoIPage.forEach(commodityVo -> {
            List<String> split = StrUtil.split(commodityVo.getBusinessSuitable(), ",");
            commodityVo.setBusinessSuitableList(split);
        });
        return Result.success(commodityCategoryVoIPage);
    }

    /**
     * []
     *
     * @param commodityId
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/7
     **/
    @Override
    public Result updateAvailableStatus(String commodityId) {
        Commodity commodity = commodityMapper.selectById(commodityId);
        if (commodity == null){
            return Result.fail("商品不存在");
        }

        if (commodity.getAvailableStatus() == 1){
            commodity.setAvailableStatus(0);
        }else {
            commodity.setAvailableStatus(1);
        }
        commodityMapper.updateById( commodity);
        return Result.success("变更状态成功");
    }
}
