package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.ServerResourcesRenewalDto;
import com.ruoyi.system.http.Result;

/**
 * 服务器资源表(ServerResourcesRenewal)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-28 10:48:27
 */
public interface IServerResourcesRenewalService{


    /**
     * [续费设置变更]
     * @author 陈湘岳 2026/3/5
     * @param serverResourcesRenewalDto 变更入参
     * @return com.ruoyi.system.http.Result
     **/
    Result insertOrUpdate(ServerResourcesRenewalDto serverResourcesRenewalDto);

    /**
     * [根据资源id查询续费]
     * @author 陈湘岳 2026/3/5
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     **/
    Result getByResourcesId(String resourcesId);
}
