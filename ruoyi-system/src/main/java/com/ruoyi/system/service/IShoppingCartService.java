package com.ruoyi.system.service;

import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.dto.ShoppingCartDto;
import com.ruoyi.system.http.Result;

import javax.validation.Valid;

/**
 * 用户购物车(ShoppingCart)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-06 23:43:09
 */
public interface IShoppingCartService{


    /**
     * [新增或修改购物车中商品]
     * @author 陈湘岳 2025/8/9
     * @param shoppingCartDto
     * @return com.ruoyi.system.http.Result
     **/
    Result addShoppingCart(ShoppingCartDto shoppingCartDto);



    /**
     * [分页查询购物车中商品]
     * @author 陈湘岳 2025/8/9
     * @param pageBase
     * @return com.ruoyi.system.http.Result
     **/
    Result pageQuery(PageBase pageBase);


    /**
     * [批量删除购物车商品]
     * @author 陈湘岳 2025/8/9
     * @param listDto
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteByIds(ListDto listDto);
}
