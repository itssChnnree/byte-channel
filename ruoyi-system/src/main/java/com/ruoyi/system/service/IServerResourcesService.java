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
     * @param password 资源id
     * @return org.springframework.http.ResponseEntity
     **/
    ResponseEntity download(String password);


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

    /**
     * [校验xray资源状态]
     * @author 陈湘岳 2025/9/25
     * @param resourcesId
     * @return com.ruoyi.system.http.Result
     **/
    Result getResourcesStatus(String resourcesId);


    /**
     * [网络连通性检测，检测与资源节点之间的网络联通性]
     * @author 陈湘岳 2025/9/26
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result networkConnectivityTesting(String resourcesId);


    /**
     * [获取检测服务状态]
     * @author 陈湘岳 2025/9/26
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result getXrayPing(String resourcesId);


    /**
     * [检测资源节点防火墙是否开放xray端口]
     * @author 陈湘岳 2025/9/26
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result getXrayFirewalld(String resourcesId);

    /**
     * [获取资源节点xray进程状态]
     * @author 陈湘岳 2025/9/26
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result getXrayProcess(String resourcesId);
}
