package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.dto.ShoppingCartDto;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.ShoppingCart;
import com.ruoyi.system.domain.vo.ShoppingCartCommodityVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.ShoppingCartMapper;
import com.ruoyi.system.mapstruct.ShoppingCartMapstruct;
import com.ruoyi.system.service.IShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户购物车(ShoppingCart)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-06 23:43:10
 */
@Service("shoppingCartService")
public class ShoppingCartServiceImpl implements IShoppingCartService {


}
