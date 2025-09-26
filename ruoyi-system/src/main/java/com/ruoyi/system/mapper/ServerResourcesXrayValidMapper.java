package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.IpUseNum;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ServerResourcesXrayValid;

import java.util.List;
import java.util.Map;

/**
 * 资源状态检查节点分布(ServerResourcesXrayValid)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-05 17:32:31
 */
@Mapper
@Repository
public interface ServerResourcesXrayValidMapper extends BaseMapper<ServerResourcesXrayValid> {


    /**
     * [查询使用最少的ip]
     * @author 陈湘岳 2025/9/24
     * @return com.ruoyi.system.domain.entity.ServerResourcesXrayValid
     **/
    @MapKey("webIpPort")
    Map<String, IpUseNum> findUseLeastIp(@Param("ipList") List<String> ipList);


    /**
     * [插入数据]
     * @author 陈湘岳 2025/9/24
     * @param serverResourcesXrayValid 插入的数据
     * @return int
     **/
    int insert(ServerResourcesXrayValid serverResourcesXrayValid);


    /**
     * [根据资源id删除校验服务器]
     * @author 陈湘岳 2025/9/25
     * @param resourcesId 资源id
     * @return int
     **/
    int deleteByResourcesIdInt(@Param("resourcesId") String resourcesId);


    /**
     * [通过资源id查询资源校验]
     * @author 陈湘岳 2025/9/25
     * @param resourcesId 资源id
     * @return com.ruoyi.system.domain.entity.ServerResourcesXrayValid
     **/
    ServerResourcesXrayValid findByResourcesId(@Param("resourcesId") String resourcesId);
}
