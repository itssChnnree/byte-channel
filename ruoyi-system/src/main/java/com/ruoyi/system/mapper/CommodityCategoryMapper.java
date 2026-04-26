package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;


import com.ruoyi.system.domain.entity.CommodityCategory;

import com.ruoyi.system.domain.dto.CommodityCategoryDto;
import com.ruoyi.system.domain.vo.CommodityCategoryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品类别(CommodityCategory)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:46
 */
@Mapper
@Repository
public interface CommodityCategoryMapper extends BaseMapper<CommodityCategory> {


    /**
     * [分页查询商品分类]
     * @author 陈湘岳 2025/7/29
     * @param commodityCategoryDto 参数
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ruoyi.system.domain.vo.CommodityCategoryVo>
     **/
    List<CommodityCategoryVo> queryPage(@Param("dto") CommodityCategoryDto commodityCategoryDto);


    /**
     * [用户分页查询商品分类]
     * @author 陈湘岳 2025/7/29
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ruoyi.system.domain.vo.CommodityCategoryVo>
     **/
    List<CommodityCategoryVo> userPage();


    /**
     * [用户分页查询商品分类]
     * @author 陈湘岳 2025/7/29
     * @return com.baomidou.mybatisplus.core.metadata.IPage<com.ruoyi.system.domain.vo.CommodityCategoryVo>
     **/
    List<CommodityCategoryVo> systemPage();

    /**
     * [查询商品分类总数]
     * @author 陈湘岳 2026/4/6
     * @return java.lang.Long
     **/
    Long countTotal();
}
