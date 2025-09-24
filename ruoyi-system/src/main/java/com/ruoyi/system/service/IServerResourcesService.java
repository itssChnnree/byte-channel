package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.ResourceProcessingDto;
import com.ruoyi.system.domain.dto.ServerResourcesPageDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.http.Result;
import org.springframework.http.ResponseEntity;

import javax.validation.Valid;

/**
 * 服务器资源表(ServerResources)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
public interface IServerResourcesService{




    /**
     * [新增服务器资源]
     * @author 陈湘岳 2025/7/23
     * @param serverResources 服务器资源
     * @return com.ruoyi.system.domain.entity.ServerResources
     **/
    Result<ServerResources> insert(ServerResourcesDto serverResources);

    /**
     * [处理节点上报资源]
     * @author 陈湘岳 2025/9/16
     * @param resourceProcessingDto 上报参数
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.entity.ServerResources>
     **/
    Result<ServerResources> resourceProcessing(@Valid ResourceProcessingDto resourceProcessingDto);

    /**
     * [资源分页查询]
     * @author 陈湘岳 2025/9/17
     * @param serverResourcesPageDto
     * @return com.ruoyi.system.http.Result
     **/
    Result getResourcesPage(ServerResourcesPageDto serverResourcesPageDto);




    /**
     * [下载clash配置文件]
     * @author 陈湘岳 2025/9/17
     * @param resourceId 资源id
     * @return org.springframework.http.ResponseEntity
     **/
    ResponseEntity download(String resourceId,String password);


    /**
     * [获取导入链接]
     * @author 陈湘岳 2025/9/22
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result getImportUrl(String resourcesId,Boolean hasAuth);


    /**
     * [重置资源]
     * @author 陈湘岳 2025/9/24
     * @param id 资源id
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.entity.ServerResources>
     **/
    Result<ServerResources> resourceReset(String id);
}
