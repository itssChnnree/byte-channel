package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.ServerResources;
import org.apache.ibatis.annotations.Param;
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
}
