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

    @Resource
    private ShoppingCartMapper shoppingCartMapper;

    @Resource
    private CommodityMapper commodityMapper;

    @Resource
    private ShoppingCartMapstruct shoppingCartMapstruct;


    /**
     * [新增或更新商品信息]
     * @author 陈湘岳 2025/8/9
     * @param shoppingCartDto
     * @return com.ruoyi.system.http.Result
     **/
    @Override
    public Result addShoppingCart(ShoppingCartDto shoppingCartDto) {
        Commodity commodity = commodityMapper.findNormalCommodity(shoppingCartDto.getCommodityId());
        if (commodity == null) {
            return Result.fail("商品不存在");
        }
        ShoppingCart shoppingCart = shoppingCartMapstruct.changeDto2(shoppingCartDto);
        shoppingCart.setUserId(SecurityUtils.getStrUserId());
        int i = shoppingCartMapper.insertOrUpdate(shoppingCart);
        return i > 0 ? Result.success() : Result.fail("添加购物车失败");
    }


    /**
     * [分页查询购物车中商品]
     *
     * @param pageBase
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/9
     **/
    @Override
    public Result pageQuery(PageBase pageBase) {
        Page<ShoppingCart> page = new Page<>(pageBase.getPageNum(), pageBase.getPageSize());
        List<ShoppingCartCommodityVo> byUserId = shoppingCartMapper.findByUserId(page, SecurityUtils.getStrUserId());
        return Result.success(byUserId);
    }

    /**
     * [批量删除购物车商品]
     *
     * @param listDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/9
     **/
    @Override
    public Result deleteByIds(ListDto listDto) {
        if (CollectionUtils.isEmpty(listDto.getIds())){
            return Result.fail("请选择要移除的商品");
        }
        int i = shoppingCartMapper.deleteByIds(SecurityUtils.getStrUserId(),listDto.getIds());
        return i > 0 ? Result.success("已移除商品") : Result.fail("移除失败");
    }
}
