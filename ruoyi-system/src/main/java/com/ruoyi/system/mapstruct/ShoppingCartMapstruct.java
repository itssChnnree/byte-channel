package com.ruoyi.system.mapstruct;


import com.ruoyi.system.domain.dto.ShoppingCartDto;
import com.ruoyi.system.domain.vo.ShoppingCartVo;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.ruoyi.system.domain.entity.ShoppingCart;


/**
 * 用户购物车(ShoppingCart)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-06 23:43:08
 */
@Mapper(componentModel = "spring")
public interface ShoppingCartMapstruct {
    ShoppingCartMapstruct INSTANCE = Mappers.getMapper(ShoppingCartMapstruct.class);

    /**
     * EntityתDTO
     *
     * @param shoppingCart
     * @return ShoppingCartDTO
     * @author chenxiangyue 2025-08-06 23:43:08
     **/
    ShoppingCartDto change2Dto(ShoppingCart shoppingCart);

    /**
     * DTO ת Entity
     *
     * @param shoppingCartDto
     * @return ShoppingCart
     * @author chenxiangyue 2025-08-06 23:43:08
     **/
    ShoppingCart changeDto2(ShoppingCartDto shoppingCartDto);

    /**
     * DTO ת VO
     *
     * @param shoppingCartDto
     * @return ShoppingCartVO
     * @author chenxiangyue 2025-08-06 23:43:08
     **/
    ShoppingCartVo changeDto2Vo(ShoppingCartDto shoppingCartDto);

    /**
     * VO ת DTO
     *
     * @param shoppingCartVo
     * @return ShoppingCartDTO
     * @author chenxiangyue 2025-08-06 23:43:08
     **/
    ShoppingCartDto changeVo2Dto(ShoppingCartVo shoppingCartVo);

    /**
     * Entity ת VO
     *
     * @param shoppingCart
     * @return ShoppingCartVO
     * @author chenxiangyue 2025-08-06 23:43:08
     **/
    ShoppingCartVo change2Vo(ShoppingCart shoppingCart);

}
