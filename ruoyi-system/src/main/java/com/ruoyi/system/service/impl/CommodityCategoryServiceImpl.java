package com.ruoyi.system.service.impl;



import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.entity.CommodityCategory;
import com.ruoyi.system.domain.dto.CommodityCategoryDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.vo.CommodityCategoryVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityCategoryMapper;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapstruct.CommodityCategoryMapstruct;
import com.ruoyi.system.service.ICommodityCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 商品类别(CommodityCategory)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:46
 */
@Service("commodityCategoryService")
public class CommodityCategoryServiceImpl  implements ICommodityCategoryService {

    @Resource
    private CommodityCategoryMapstruct commodityCategoryMapstruct;

    @Resource
    private CommodityCategoryMapper commodityCategoryMapper;

    @Resource
    private CommodityMapper commodityMapper;

    /**
     * 添加商品类别
     *
     * @param commodityCategoryDto 添加参数
     * @return
     */
    @Override
    public Result insert(CommodityCategoryDto commodityCategoryDto) {
        CommodityCategory commodityCategory = commodityCategoryMapstruct.changeDto2(commodityCategoryDto);
        int insert = commodityCategoryMapper.insert(commodityCategory);
        if (insert > 0){
            return Result.success(commodityCategory);
        }else {
            return Result.fail("添加失败");
        }
    }


    /**
     * [修改商品类别]
     *
     * @param commodityCategoryDto 修改参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/7/28
     **/
    @Override
    public Result update(CommodityCategoryDto commodityCategoryDto) {
        CommodityCategory commodityCategory = commodityCategoryMapstruct.changeDto2(commodityCategoryDto);
        int i = commodityCategoryMapper.updateById(commodityCategory);
        if (i > 0){
            return Result.success(commodityCategory);
        }else {
            return Result.fail("修改失败");
        }
    }


    /**
     * [根据类别id批量删除类别]
     *
     * @param listDto 类别id集合
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/7/29
     **/
    @Override
    public Result deleteByIds(ListDto listDto) {
        if (CollectionUtils.isEmpty(listDto.getIds())){
            return Result.fail("请选择要删除的类别");
        }else{
            List<String> strings = commodityMapper.haveCommodity(listDto.getIds());
            //筛选出不存在商品的类别
            List<String> collect = listDto.getIds().stream().filter(id -> !strings.contains(id)).collect(Collectors.toList());
            if (CollectionUtils.isEmpty( collect)){
                return Result.fail("所选类别下存在商品");
            }
            int delete = commodityCategoryMapper.deleteBatchIds(collect);
            if (delete > 0){
                return Result.success("已删除未存在商品的类别");
            }else {
                return Result.fail("删除失败");
            }
        }
    }

    /**
     * [分页查询商品类别]
     *
     * @param listDto 查询参数
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/7/29
     **/
    @Override
    public Result page(CommodityCategoryDto listDto) {
//        Page<CommodityCategory> page = new Page<>(listDto.getCurrentPage(), listDto.getPageSize());
        PageHelper.startPage(listDto);
        List<CommodityCategoryVo> commodityCategoryVoIPage = commodityCategoryMapper.queryPage(listDto);
        return Result.success(new PageInfo<>(commodityCategoryVoIPage));
    }


    /**
     * [用户分页查询商品类别]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/7/29
     **/
    @Override
    public Result userPage() {
        List<CommodityCategoryVo> commodityCategoryVoIPage = commodityCategoryMapper.userPage();
        return Result.success(commodityCategoryVoIPage);
    }


    /**
     * [用户分页查询商品类别]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/7/29
     **/
    @Override
    public Result systemPage() {
        List<CommodityCategoryVo> commodityCategoryVoIPage = commodityCategoryMapper.systemPage();
        return Result.success(commodityCategoryVoIPage);
    }
}
