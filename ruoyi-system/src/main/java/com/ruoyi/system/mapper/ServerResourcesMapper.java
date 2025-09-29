package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.ServerResourcesPageDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.entity.ResourceAllocationTemporaryStorage;
import com.ruoyi.system.domain.vo.ServerResourcesDetailVo;
import com.ruoyi.system.domain.vo.ServerResourcesPageVo;
import com.ruoyi.system.domain.vo.ServerResourcesVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 服务器资源表(ServerResources)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Mapper
@Repository
public interface ServerResourcesMapper extends BaseMapper<ServerResources> {


    /**
     * [查询存在资源的商品集合]
     * @author 陈湘岳 2025/8/1
     * @param commodityIdList 商品id集合
     * @return java.util.List<java.lang.String>
     **/
    List<String> haveServerResources(@Param("commodityIdList") List<String> commodityIdList);


    /**
     * [查询存在资源的账号]
     * @author 陈湘岳 2025/8/1
     * @param accountId 商品id集合
     * @return java.util.List<java.lang.String>
     **/
    Integer haveServerResourcesByAccount(@Param("accountId") String accountId);


    /**
     * [修改资源可售状态及释放租售人]
     * @author 陈湘岳 2025/8/22
     * @param serverResourcesIds 资源id集合
     * @return int
     **/
    int updateServerResourcesSaleStatus(@Param("resourcesId") List<String> serverResourcesIds);

    /**
     * [修改资源租赁时间-年]
     * @author 陈湘岳 2025/8/22
     * @param serverResourcesIds 资源id集合
     * @return int
     **/
    int updateLeaseExpirationTimeYear(@Param("resourcesId") List<String> serverResourcesIds);

    /**
     * [修改资源租赁时间-月]
     * @author 陈湘岳 2025/8/22
     * @param serverResourcesIds 资源id集合
     * @return int
     **/
    int updateLeaseExpirationTimeMonth(@Param("resourcesId") List<String> serverResourcesIds);


    /**
     * [修改资源租赁时间-季]
     * @author 陈湘岳 2025/8/22
     * @param serverResourcesIds 资源id集合
     * @return int
     **/
    int updateLeaseExpirationTimeQuarter(@Param("resourcesId") List<String> serverResourcesIds);


    /**
     * [通过ip查询暂存数据及资源数据]
     * @author 陈湘岳 2025/9/16
     * @param ip
     * @return ResourceAllocationTemporaryStorage
     **/
    ResourceAllocationTemporaryStorage findByIp(@Param("ip") String ip);


    /**
     * [通过ip查询相关数据]
     * @author 陈湘岳 2025/9/16
     * @param ip
     * @return ResourceAllocationTemporaryStorage
     **/
    ServerResources findByIpServerResources(@Param("ip") String ip);


    /**
     * [服务器资源管理分页查询]
     * @author 陈湘岳 2025/9/17
     * @param serverResourcesPageDto 查询入参
     * @return java.util.List<com.ruoyi.system.domain.vo.ServerResourcesPageVo>
     **/
    List<ServerResourcesPageVo> findPage(@Param("dto") ServerResourcesPageDto serverResourcesPageDto);

    /**
     * [根据资源id和租户id查询]
     * @author 陈湘岳 2025/9/22
     * @param password 随机生成密码
     * @return com.ruoyi.system.domain.entity.ServerResources
     **/
    ServerResources selectByPassword(@Param("password") String password);


    /**
     * [通过资源id变更资源状态]
     * @author 陈湘岳 2025/9/25
     * @param resourcesId 资源id
     * @param resourcesStatus 资源状态
     * @return int
     **/
    int updateResourcesStatus(@Param("resourcesId") String resourcesId,@Param("resourcesStatus")String resourcesStatus);


    /**
     * [资源详情查询]
     * @author 陈湘岳 2025/9/28
     * @param id 资源id
     * @return com.ruoyi.system.domain.vo.ServerResourcesVo
     **/
    ServerResourcesDetailVo getById(@Param("id") String id);
}
