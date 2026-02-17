package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.framework.web.service.PermissionService;
import com.ruoyi.system.domain.dto.*;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.vo.ServerResourcesDetailVo;
import com.ruoyi.system.domain.vo.ServerResourcesVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IServerResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.Cleanup;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * [˵��/����]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 **/
@Api(tags = "服务器资源表")
@RestController
@RequestMapping("/serverResources")

public class ServerResourcesController{

    @Resource(name = "serverResourcesService")
    IServerResourcesService serverResourcesService;

    @Resource
    private PermissionService permissionService;


    @PostMapping("/insert")
    @ApiOperation("添加服务器资源")
    public Result<ServerResources> insert(@RequestBody @Valid ServerResourcesDto serverResources, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return serverResourcesService.insert(serverResources);
    }


    @PostMapping("/resourceProcessing")
    @ApiOperation("处理从节点上报的资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result<ServerResources> resourceProcessing(@RequestBody @Valid ResourceProcessingDto resourceProcessingDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return serverResourcesService.resourceProcessing(resourceProcessingDto);
    }



    @PostMapping("/resourceReset")
    @ApiOperation("重置节点")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result<ServerResources> resourceReset(@RequestBody IdDto idDto) {
        if (StrUtil.isBlank(idDto.getId())){
            return Result.fail("资源编号不能为空");
        }
        return serverResourcesService.resourceReset(idDto.getId());
    }


    @PostMapping("/ipReplacement")
    @ApiOperation("ip置换")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result<ServerResources> ipReplacement(@RequestBody @Valid ServerUpdateDto serverUpdateDto, BindingResult bindingResult) {
        if (bindingResult.hasErrors()){
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return serverResourcesService.ipReplacement(serverUpdateDto);
    }


    @DeleteMapping("/deleteById")
    @ApiOperation("通过资源id删除资源")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result deleteById(String id) {
        if (StrUtil.isBlank(id)){
            return Result.fail("资源id不能为空");
        }
        return serverResourcesService.deleteById(id);
    }



    @GetMapping("/getById")
    @ApiOperation("查询详情")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result<ServerResourcesDetailVo> getById(String id) {
        if (StrUtil.isBlank( id)){
            return Result.fail("资源编号不能为空");
        }
        return serverResourcesService.getById(id);
    }



    @GetMapping("/getResourcesStatus")
    @ApiOperation("获取资源状态")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getResourcesStatus(String resourcesId) {
        if (StrUtil.isBlank(resourcesId)){
            return Result.fail("资源编号不能为空");
        }
        return serverResourcesService.getResourcesStatus(resourcesId);
    }



    @GetMapping("/networkConnectivityTesting")
    @ApiOperation("网络连通性检测")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result networkConnectivityTesting(String resourcesId) {
        if (StrUtil.isBlank(resourcesId)){
            return Result.fail("资源编号不能为空");
        }
        return serverResourcesService.networkConnectivityTesting(resourcesId);
    }



    @GetMapping("/getXrayPing")
    @ApiOperation("检测资源节点Go服务状态")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getXrayPing(String resourcesId) {
        if (StrUtil.isBlank(resourcesId)){
            return Result.fail("资源编号不能为空");
        }
        return serverResourcesService.getXrayPing(resourcesId);
    }


    @GetMapping("/getXrayFirewalld")
    @ApiOperation("检测资源节点防火墙是否开放")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getXrayFirewalld(String resourcesId) {
        if (StrUtil.isBlank(resourcesId)){
            return Result.fail("资源编号不能为空");
        }
        return serverResourcesService.getXrayFirewalld(resourcesId);
    }


    @GetMapping("/getXrayProcess")
    @ApiOperation("检测资源节点xray进程")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getXrayProcess(String resourcesId) {
        if (StrUtil.isBlank(resourcesId)){
            return Result.fail("资源编号不能为空");
        }
        return serverResourcesService.getXrayProcess(resourcesId);
    }



    @PostMapping("/getResourcesPage")
    @ApiOperation("资源分页查询")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getResourcesPage(@RequestBody ServerResourcesPageDto serverResourcesPageDto) {
        return serverResourcesService.getResourcesPage(serverResourcesPageDto);
    }

    @PostMapping("/getUserResourcesPage")
    @ApiOperation("用户资源分页查询")
    public Result getUserResourcesPage(@RequestBody ServerResourcesPageDto serverResourcesPageDto) {
        return serverResourcesService.getUserResourcesPage(serverResourcesPageDto);
    }


    //下载clash配置文件
    @GetMapping("/download")
    @ApiOperation("下载clash配置文件")
    public ResponseEntity download(String password){
        return serverResourcesService.download(password);
    }


    //下载clash配置文件
    @GetMapping("/getImportUrl")
    @ApiOperation("获取导入链接")
    public Result getImportUrl(String resourcesId){
        return serverResourcesService.getImportUrl(resourcesId, SecurityUtils.hasPermi());
    }



    @GetMapping("/getOrderAdd")
    @ApiOperation("获取新购资源商品快照")
    public Result getOrderAdd(String orderId){
        return serverResourcesService.getOrderAdd(orderId);
    }

    @GetMapping("/getOrderRenewal")
    @ApiOperation("获取续费资源商品快照")
    public Result getOrderRenewal(String orderId){
        return serverResourcesService.getOrderRenewal(orderId);
    }


}
