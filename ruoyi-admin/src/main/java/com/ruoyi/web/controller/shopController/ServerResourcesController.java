package com.ruoyi.web.controller.shopController;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.Page;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.framework.web.service.PermissionService;
import com.ruoyi.system.domain.dto.IdDto;
import com.ruoyi.system.domain.dto.ResourceProcessingDto;
import com.ruoyi.system.domain.dto.ServerResourcesPageDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
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




    @PostMapping("/getResourcesPage")
    @ApiOperation("资源分页查询")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getResourcesPage(@RequestBody ServerResourcesPageDto serverResourcesPageDto) {
        return serverResourcesService.getResourcesPage(serverResourcesPageDto);
    }


    //下载clash配置文件
    @GetMapping("/download")
    @ApiOperation("下载clash配置文件")
    public ResponseEntity download(String resourcesId,String password){
        return serverResourcesService.download(resourcesId,password);
    }


    //下载clash配置文件
    @GetMapping("/getImportUrl")
    @ApiOperation("获取导入链接")
    public Result getImportUrl(String resourcesId){
        return serverResourcesService.getImportUrl(resourcesId, permissionService.hasPermi("shop:background:admin"));
    }



}
