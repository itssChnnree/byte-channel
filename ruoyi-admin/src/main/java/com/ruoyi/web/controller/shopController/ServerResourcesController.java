package com.ruoyi.web.controller.shopController;

import com.github.pagehelper.Page;
import com.ruoyi.common.utils.uuid.UUID;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IServerResourcesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
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


    @PostMapping("/insert")
    @ApiOperation("添加服务器资源")
    public Result<ServerResources> insert(@RequestBody @Valid ServerResourcesDto serverResources, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.fail(bindingResult.getAllErrors().get(0).getDefaultMessage());
        }
        return serverResourcesService.insert(serverResources);
    }



    @GetMapping("/test")
    public Result<Page<ServerResources>> test() {
        //随机生成一个返回对象
        Page<ServerResources> page = new Page<>(1, 10);
        page.setTotal(100);
        List<ServerResources> list = new ArrayList<>();
        //模拟数据
        for (int i = 0; i < 10; i++) {
            ServerResources serverResources = new ServerResources();
            serverResources.setId(UUID.fastUUID().toString());
            serverResources.setResourcesIp("192.168.1."+i);
            serverResources.setResourcesPort("8080");
            serverResources.setResourcesStatus("normal");
            serverResources.setResourceTenant("admin");
            serverResources.setLeaseExpirationTime(new Date());
            serverResources.setSalesStatus("normal");
            serverResources.setAvailableStatus(1);
            serverResources.setNodeUrl("http://192.168.1."+i+":8080");
            list.add(serverResources);
        }
        page.addAll(list);
        return Result.success(page);
    }
}
