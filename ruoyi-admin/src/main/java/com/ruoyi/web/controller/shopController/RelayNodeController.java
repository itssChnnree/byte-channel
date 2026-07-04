package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.ruoyi.system.domain.dto.RelayNodeDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IRelayNodeService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

@Api(tags = "中转节点表")
@RestController
@RequestMapping("relayNode")
public class RelayNodeController {

    @Resource(name = "relayNodeService")
    IRelayNodeService relayNodeService;

    @PostMapping("/insert")
    @ApiOperation("新增节点")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result insert(@RequestBody RelayNodeDto dto) {
        return relayNodeService.insert(dto);
    }

    @PostMapping("/update")
    @ApiOperation("编辑节点")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result update(@RequestBody RelayNodeDto dto) {
        return relayNodeService.update(dto);
    }

    @DeleteMapping("/delete")
    @ApiOperation("删除节点")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result delete(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("节点ID不能为空");
        }
        return relayNodeService.delete(id);
    }

    @GetMapping("/page")
    @ApiOperation("分页查询节点")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(RelayNodeDto dto) {
        return relayNodeService.page(dto);
    }

    @GetMapping("/list")
    @ApiOperation("查询全部节点")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result list() {
        return relayNodeService.list();
    }

    @GetMapping("/findById")
    @ApiOperation("查询节点详情")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result findById(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("节点ID不能为空");
        }
        return relayNodeService.findById(id);
    }

    @PutMapping("/toggleStatus")
    @ApiOperation("启用/停用节点")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result toggleStatus(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("节点ID不能为空");
        }
        return relayNodeService.toggleStatus(id);
    }
}
