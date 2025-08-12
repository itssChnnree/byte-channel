package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.vo.ShoppingCartCommodityVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ShoppingCart;

import java.util.List;

/**
 * 用户购物车(ShoppingCart)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-06 23:43:09
 */
@Mapper
@Repository
public interface ShoppingCartMapper extends BaseMapper<ShoppingCart> {



    /**
     * [批量新增或修改商品]
     * @author 陈湘岳 2025/8/9
     * @param list 数量
     * @return int
     **/
    int insertOrUpdateBatch(@Param("entities") List<ShoppingCart> list);

    /**
     * [新增或修改商品]
     * @author 陈湘岳 2025/8/9
     * @param shoppingCart 数据
     * @return int
     **/
    int insertOrUpdate(@Param("entity") ShoppingCart shoppingCart);


    /**
     * [分页查询购物车中商品]
     * @author 陈湘岳 2025/8/9
     * @param page 分页参数
     * @param userId 用户id
     * @return java.util.List<com.ruoyi.system.domain.vo.ShoppingCartCommodityVo>
     **/
    List<ShoppingCartCommodityVo> findByUserId(Page<ShoppingCart> page, @Param("userId") String userId);


    /**
     * [批量删除购物车]
     * @author 陈湘岳 2025/8/9
     * @param ids 购物车id
     * @return int
     **/
    int deleteByIds(@Param("userId")String userId,@Param("list") List<String> ids);
}
