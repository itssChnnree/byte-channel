package com.ruoyi.web.controller.shopController;


import com.ruoyi.system.domain.base.PageBase;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.domain.dto.ShoppingCartDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IShoppingCartService;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-06 23:43:10
 **/
@Api(tags = "用户购物车")
@RestController
@RequestMapping("shoppingCart")
public class ShoppingCartController{

    @Resource(name = "shoppingCartService")
    IShoppingCartService shoppingCartService;


}
