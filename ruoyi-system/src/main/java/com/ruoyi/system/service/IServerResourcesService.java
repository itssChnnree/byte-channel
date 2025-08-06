package com.ruoyi.system.service;


import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.http.Result;

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
}
