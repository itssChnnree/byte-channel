package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.RelayResourceDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IRelayResourceService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

@Api(tags = "中转资源表")
@RestController
@RequestMapping("relayResource")
public class RelayResourceController {

    @Resource(name = "relayResourceService")
    IRelayResourceService relayResourceService;

    @PostMapping("/insert")
    @ApiOperation("新增资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result insert(@RequestBody RelayResourceDto dto) {
        return relayResourceService.insert(dto);
    }

    @PostMapping("/update")
    @ApiOperation("编辑资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody RelayResourceDto dto) {
        return relayResourceService.update(dto);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result delete(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("资源ID不能为空");
        }
        return relayResourceService.delete(id);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(RelayResourceDto dto) {
        return relayResourceService.page(dto);
    }

    @GetMapping("/list")
    @ApiOperation("查询全部资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result list() {
        return relayResourceService.list();
    }

    @GetMapping("/findById")
    @ApiOperation("查询资源详情")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result findById(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("资源ID不能为空");
        }
        return relayResourceService.findById(id);
    }

    @PutMapping("/toggleStatus")
    @ApiOperation("启用/停用资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result toggleStatus(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("资源ID不能为空");
        }
        return relayResourceService.toggleStatus(id);
    }
}
