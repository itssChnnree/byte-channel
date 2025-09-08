package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.CommodityCategoryDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.http.Result;

import javax.validation.Valid;


/**
 * 商品类别(CommodityCategory)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:46
 */
public interface ICommodityCategoryService {


    /**
     * 添加商品类别
     *
     * @param commodityCategoryDto 添加参数
     * @return
     */
    Result insert(@Valid CommodityCategoryDto commodityCategoryDto);

    /**
     * [修改商品类别]
     * @author 陈湘岳 2025/7/28
     * @param commodityCategoryDto 修改参数
     * @return com.ruoyi.system.http.Result
     **/
    Result update(CommodityCategoryDto commodityCategoryDto);


    /**
     * [根据类别id批量删除类别]
     * @author 陈湘岳 2025/7/29
     * @param listDto 类别id集合
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteByIds(ListDto listDto);

    /**
     * [分页查询商品类别]
     * @author 陈湘岳 2025/7/29
     * @param listDto 查询参数
     * @return com.ruoyi.system.http.Result
     **/
    Result page(CommodityCategoryDto listDto);


    /**
     * [用户分页查询商品类别]
     * @author 陈湘岳 2025/7/29
     * @return com.ruoyi.system.http.Result
     **/
    Result userPage();

    /**
     * [系统分页查询商品类别]
     * @author 陈湘岳 2025/7/29
     * @return com.ruoyi.system.http.Result
     **/
    Result systemPage();
}
