package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
    @MapKey("web_ip")
    Map<String,Integer> findUseLeastIp(@Param("ipList") List<String> ipList);


    /**
     * [插入数据]
     * @author 陈湘岳 2025/9/24
     * @param serverResourcesXrayValid 插入的数据
     * @return int
     **/
    int insert(ServerResourcesXrayValid serverResourcesXrayValid);

}
