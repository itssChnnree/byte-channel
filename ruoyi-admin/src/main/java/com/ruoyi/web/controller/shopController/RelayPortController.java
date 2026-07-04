package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.RelayPortDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IRelayPortService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

@Api(tags = "中转端口表")
@RestController
@RequestMapping("relayPort")
public class RelayPortController {

    @Resource(name = "relayPortService")
    IRelayPortService relayPortService;

    @PostMapping("/insert")
    @ApiOperation("新增端口")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result insert(@RequestBody RelayPortDto dto) {
        return relayPortService.insert(dto);
    }

    @PostMapping("/update")
    @ApiOperation("编辑端口")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody RelayPortDto dto) {
        return relayPortService.update(dto);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除端口")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result delete(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("端口ID不能为空");
        }
        return relayPortService.delete(id);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询端口")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(RelayPortDto dto) {
        return relayPortService.page(dto);
    }

    @GetMapping("/findById")
    @ApiOperation("查询端口详情")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result findById(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("端口ID不能为空");
        }
        return relayPortService.findById(id);
    }
}
