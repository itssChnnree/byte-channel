package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.ResourceProcessingDto;
import com.ruoyi.system.domain.dto.ServerResourcesPageDto;
import com.ruoyi.system.domain.dto.ServerUpdateDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.domain.entity.ServerResourcesXrayValid;
import com.ruoyi.system.domain.vo.ServerResourcesDetailVo;
import com.ruoyi.system.domain.vo.ServerResourcesVo;
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
     * [校验xray资源状态]
     * @author 陈湘岳 2025/9/25
     * @param resourcesId
     * @return com.ruoyi.system.http.Result
     **/
    Result getResourcesStatusByUser(String resourcesId);


    /**
     * [网络连通性检测，检测与资源节点之间的网络联通性]
     * @author 陈湘岳 2025/9/26
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result networkConnectivityTesting(String resourcesId);

    /**
     * [新增资源检测]
     * @author 陈湘岳 2026/3/30
     * @param userId 节点的userId
     * @param serverResources 资源
     * @return com.ruoyi.system.domain.entity.ServerResourcesXrayValid
     **/
    ServerResourcesXrayValid newResourcesValid(String userId, ServerResources serverResources);


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

    /**
     * [详情查询]
     * @author 陈湘岳 2025/9/28
     * @param id
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.vo.ServerResourcesVo>
     **/
    Result<ServerResourcesDetailVo> getById(String id);

    /**
     * [ip置换]
     * @author 陈湘岳 2025/9/28
     * @param serverUpdateDto 置换参数
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.entity.ServerResources>
     **/
    Result<ServerResources> ipReplacement(@Valid ServerUpdateDto serverUpdateDto);

    /**
     * [用户查询资源]
     * @author 陈湘岳 2025/10/27
     * @param serverResourcesPageDto 查询参数
     * @return com.ruoyi.system.http.Result
     **/
    Result getUserResourcesPage(ServerResourcesPageDto serverResourcesPageDto);

    /**
     * [获取新购订单资源详情]
     * @author 陈湘岳 2025/11/20
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result getOrderAdd(String orderId);

    /**
     * [通过资源id删除资源]
     * @author 陈湘岳 2025/12/15
     * @param id 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteById(String id);

    /**
     * [获取续费类型资源订单快照]
     * @author 陈湘岳 2026/1/23
     * @param orderId 订单id
     * @return com.ruoyi.system.http.Result
     **/
    Result getOrderRenewal(String orderId);

    /**
     * [用户重置节点]
     * @author 陈湘岳 2026/3/7
     * @param id 资源id
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.entity.ServerResources>
     **/
    Result<ServerResources> userResourceReset(String id);


    /**
     * [资源检测定时任务]
     * 查询所有资源，通过多线程对每个资源进行连通性检测
     * @author 陈湘岳 2026/3/18
     * @return void
     **/
    void resourceDetectionTask();

    /**
     * [切换资源上下架状态]
     * 上架变为下架，下架变为上架
     * @author 陈湘岳 2026/3/23
     * @param id 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result toggleAvailableStatus(String id);

    /**
     * [更新到期资源]
     * 查询租赁到期时间小于当前时间减去30分钟的资源，
     * 将salesStatus设置为未售出，并将租赁到期时间和资源当前租户设置为null
     * @author 陈湘岳 2026/3/23
     * @return int 清理的资源数量
     **/
    int cleanExpiredResources();

    /**
     * [查询可售商品]
     * 上架变为下架，下架变为上架
     * @author 陈湘岳 2026/3/28
     * @return com.ruoyi.system.http.Result
     **/
    ServerResources findByCommodityId(String commodityId);

    /**
     * [资源置换]
     * @author 陈湘岳 2026/4/7
     * @param id 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result resourceReplacement(String id);
}
