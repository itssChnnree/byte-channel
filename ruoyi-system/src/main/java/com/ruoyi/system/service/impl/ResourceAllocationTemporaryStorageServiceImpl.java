package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.dto.ResourceAllocationTemporaryStorageDto;
import com.ruoyi.system.domain.entity.ResourceAllocationTemporaryStorage;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.ResourceAllocationTemporaryStorageMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapstruct.ResourceAllocationTemporaryStorageMapstruct;
import com.ruoyi.system.service.IResourceAllocationTemporaryStorageService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 资源暂存表(ResourceAllocationTemporaryStorage)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:59
 */
@Service("resourceAllocationTemporaryStorageService")
public class ResourceAllocationTemporaryStorageServiceImpl  implements IResourceAllocationTemporaryStorageService {

    @Resource
    private ResourceAllocationTemporaryStorageMapper resourceAllocationTemporaryStorageMapper;

    @Resource
    private ResourceAllocationTemporaryStorageMapstruct resourceAllocationTemporaryStorageMapstruct;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    /**
     * [新增资源暂存数据]
     *
     * @param resourceAllocationTemporaryStorageDto
     * @return java.lang.String
     * @author 陈湘岳 2025/9/12
     **/
    @Override
    public Result add(ResourceAllocationTemporaryStorageDto resourceAllocationTemporaryStorageDto) {
        ResourceAllocationTemporaryStorage byIp = serverResourcesMapper.findByIp(resourceAllocationTemporaryStorageDto.getResourcesIp());
        if (byIp != null){
            LogEsUtil.info("ip重复新增");
            return Result.success(byIp);
        }
        ResourceAllocationTemporaryStorage resourceAllocationTemporaryStorage = resourceAllocationTemporaryStorageMapstruct.changeDto2(resourceAllocationTemporaryStorageDto);
        resourceAllocationTemporaryStorage.setCreateTime(new Date());
        int insert = resourceAllocationTemporaryStorageMapper.insert(resourceAllocationTemporaryStorage);
        if (insert<=0){
            LogEsUtil.warn("新增资源暂存失败："+resourceAllocationTemporaryStorage);
            return Result.fail("新增失败");
        }
        LogEsUtil.info("新增资源暂存成功："+resourceAllocationTemporaryStorage);
        return Result.success(resourceAllocationTemporaryStorage);
    }


    /**
     * [分页查询资源暂存数据]
     *
     * @return java.lang.String
     * @author 陈湘岳 2025/9/12
     **/
    @Override
    public Result page(String ip) {
        return Result.success(resourceAllocationTemporaryStorageMapper.page(ip));
    }


    /**
     * [根据暂存id删除数据]
     *
     * @param id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/9/12
     **/
    @Override
    public Result deleteById(String id) {
        int i = resourceAllocationTemporaryStorageMapper.deleteById(id);
        if (i>0){
            LogEsUtil.info("删除暂存资源成功："+id);
            return Result.success("删除成功");
        }else {
            LogEsUtil.warn("删除暂存资源失败："+id);
            return Result.fail("删除失败");
        }
    }
}
