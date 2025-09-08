package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.CommodityCategoryDto;
import com.ruoyi.system.domain.dto.CommodityDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.http.Result;

/**
 * 商品表(Commodity)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:33
 */
public interface ICommodityService {


    /**
     * [创建商品]
     * @author 陈湘岳 2025/7/29
     * @param commodityDto 创建商品参数
     * @return com.ruoyi.system.http.Result
     **/
    Result insert(CommodityDto commodityDto);

    /**
     * [修改商品]
     * @author 陈湘岳 2025/8/1
     * @param commodityDto 商品修改入参
     * @return com.ruoyi.system.http.Result
     **/
    Result update(CommodityDto commodityDto);


    /**
     * [删除商品]
     * @author 陈湘岳 2025/8/1
     * @param listDto 删除商品id集合
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteByIds(ListDto listDto);


    /**
     * [分页查询商品]
     * @author 陈湘岳 2025/8/1
     * @param commodityCategoryDto 查询商品参数
     * @return com.ruoyi.system.http.Result
     **/
    Result page(CommodityDto commodityCategoryDto);


    /**
     * [通过详情查询商品信息]
     * @author 陈湘岳 2025/8/1
     * @param id 通过详情查询商品信息
     * @return com.ruoyi.system.http.Result
     **/
    Result findById(String id);


    /**
     * [分页查询商品]
     * @author 陈湘岳 2025/8/31
     * @param commodityDto 查询参数
     * @return com.ruoyi.system.http.Result
     **/
    Result userPage(CommodityDto commodityDto);

    
    /**
     * [] 
     * @author 陈湘岳 2025/9/7
     * @param commodityId
     * @return com.ruoyi.system.http.Result
     **/
    Result updateAvailableStatus(String commodityId);



}
