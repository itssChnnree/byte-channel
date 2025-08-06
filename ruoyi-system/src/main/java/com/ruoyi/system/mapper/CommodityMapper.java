package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.dto.CommodityDto;
import com.ruoyi.system.domain.vo.CommodityVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 商品表(Commodity)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:33
 */
@Mapper
@Repository
public interface CommodityMapper extends BaseMapper<Commodity> {



//    /**
//     * [根据类别id查询类]
//     * @author 陈湘岳 2025/7/29
//     * @param categoryId 类别id
//     * @return java.util.List<com.ruoyi.system.domain.entity.Commodity>
//     **/
//    List<Commodity> findByCategoryId(@Param("categoryId") String categoryId);

    /**
     * [根据类别id查询是否存在所属商品]
     * @author 陈湘岳 2025/7/29
     * @param categoryIds 类别id集合
     * @return int
     **/
    List<String> haveCommodity(@Param("categoryIds") List<String> categoryIds);

    /**
     * [更新商品信息]
     * @author 陈湘岳 2025/7/29
     * @param commodity 商品信息
     * @return int
     **/
    int update(Commodity commodity);

    /**
     * [分页查询商品信息]
     * @author 陈湘岳 2025/7/29
     * @param commodityDto 商品信息
     * @return java.util.List<com.ruoyi.system.domain.vo.CommodityVo>
     **/
    List<CommodityVo> queryPage(@Param("dto") CommodityDto commodityDto);


    /**
     * [详情查询]
     * @author 陈湘岳 2025/8/1
     * @param id
     * @return com.ruoyi.system.domain.vo.CommodityVo
     **/
    CommodityVo queryById(@Param("id") String id);
}
