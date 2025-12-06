package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.ShopNoticeDto;
import com.ruoyi.system.domain.vo.ShopNoticeVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IShopNoticeService;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * [商城公告表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:29
 **/
@Api(tags = "商城公告表")
@RestController
@RequestMapping("shopNotice")
public class ShopNoticeController {

    @Resource(name = "shopNoticeService")
    IShopNoticeService shopNoticeService;

    @ApiOperation("新增公告")
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result add(@RequestBody @Valid ShopNoticeDto shopNoticeDto,
                      BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return shopNoticeService.add(shopNoticeDto);
    }


    @ApiOperation("变更公告状态")
    @PutMapping("/changeStatus")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result changeStatus(String id) {
        if (StrUtil.isBlank(id)){
            return Result.fail("请选择变更状态的公告");
        }
        return shopNoticeService.changeStatus(id);
    }

    @ApiOperation("删除公告")
    @DeleteMapping("/deleteById")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteById(String id) {
        if (StrUtil.isBlank(id)){
            return Result.fail("请选择删除的公告");
        }
        return shopNoticeService.deleteById(id);
    }

    @ApiOperation("查询公告列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result list(ShopNoticeDto shopNoticeDto){
        if (shopNoticeDto.getPageNum()==null || shopNoticeDto.getPageNum()<=0){
            shopNoticeDto.setPageNum(1L);
        }
        if (shopNoticeDto.getPageSize()==null || shopNoticeDto.getPageSize()<=0){
            shopNoticeDto.setPageSize(10L);
        }
        return shopNoticeService.list(shopNoticeDto);
    }

    @ApiOperation("前台查询标题公告列表")
    @GetMapping("/titleList")
    public Result titleList(){
        return shopNoticeService.titleList();
    }

    @GetMapping("/getById")
    @ApiOperation("查询公告详情")
    public Result<ShopNoticeVo> getById(String id) {
        if (StrUtil.isBlank(id)){
            return Result.fail("请选择查询的公告");
        }
        return shopNoticeService.getById(id);
    }

    @GetMapping("/getByIdSystem")
    @ApiOperation("后台查询公告详情")
    public Result<ShopNoticeVo> getByIdSystem(String id) {
        if (StrUtil.isBlank(id)){
            return Result.fail("请选择查询的公告");
        }
        return shopNoticeService.getByIdSystem(id);
    }


    @PutMapping("/update")
    @ApiOperation("修改公告")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody @Valid ShopNoticeDto shopNoticeDto,
                         BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return shopNoticeService.update(shopNoticeDto);
    }


    @ApiOperation("用户查询公告列表")
    @GetMapping("/userList")
    public Result userList(ShopNoticeDto shopNoticeDto){
        if (shopNoticeDto.getPageNum()==null || shopNoticeDto.getPageNum()<=0){
            shopNoticeDto.setPageNum(1L);
        }
        if (shopNoticeDto.getPageSize()==null || shopNoticeDto.getPageSize()<=0){
            shopNoticeDto.setPageSize(10L);
        }
        shopNoticeDto.setStatus("0");
        return shopNoticeService.userlist(shopNoticeDto);
    }


}
