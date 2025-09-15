package com.ruoyi.system.service;

import com.ruoyi.system.domain.dto.ResourceAllocationTemporaryStorageDto;
import com.ruoyi.system.http.Result;

/**
 * 资源暂存表(ResourceAllocationTemporaryStorage)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:59
 */
public interface IResourceAllocationTemporaryStorageService{


    /**
     * [新增资源暂存数据]
     * @author 陈湘岳 2025/9/12
     * @param resourceAllocationTemporaryStorageDto
     * @return java.lang.String
     **/
    Result add(ResourceAllocationTemporaryStorageDto resourceAllocationTemporaryStorageDto);


    /**
     * [查询资源暂存数据]
     * @author 陈湘岳 2025/9/12
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result page(String ip);


    /**
     * [根据暂存id删除数据]
     * @author 陈湘岳 2025/9/12
     * @param id
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteById(String id);
}
