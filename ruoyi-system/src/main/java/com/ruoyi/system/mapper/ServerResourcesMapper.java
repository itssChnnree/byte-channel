package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.ServerResourcesPageDto;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.entity.ResourceAllocationTemporaryStorage;
import com.ruoyi.system.domain.vo.OrderNewVo;
import com.ruoyi.system.domain.vo.OrderRenewalVo;
import com.ruoyi.system.domain.vo.ServerResourcesDetailVo;
import com.ruoyi.system.domain.vo.ServerResourcesPageVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.Date;
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
     * [释放资源]
     * @author 陈湘岳 2025/8/1
     * @param resourcesId 资源id
     * @return int
     **/
    int cleanResources(@Param("resourcesId") String resourcesId);

    /**
     * [查询资源详情]
     * @author 陈湘岳 2025/8/1
     * @param resourcesId 资源id
     * @return com.ruoyi.system.domain.vo.ServerResourcesVo
     **/
    ServerResources findById(@Param("resourcesId") String resourcesId);

    /**
     * [查询资源详情带锁]
     * @author 陈湘岳 2025/8/1
     * @param resourcesId 资源id
     * @return com.ruoyi.system.domain.vo.ServerResourcesVo
     **/
    ServerResources findByIdForUpdate(@Param("resourcesId") String resourcesId);


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
     * [查询用户资源]
     * @author 陈湘岳 2025/10/27
     * @param serverResourcesPageDto 查询参数
     * @param userId 用户id
     * @param date 到期时间
     * @return java.util.List<com.ruoyi.system.domain.vo.ServerResourcesPageVo>
     **/
    List<ServerResourcesPageVo> findUserPage(@Param("dto") ServerResourcesPageDto serverResourcesPageDto,
                                             @Param("userId") String userId, @Param("time")Date date);

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

    /**
     * [通过资源id查询资源故障时间]
     * @author 陈湘岳 2025/10/10
     * @param resourcesId 资源id
     * @return java.lang.String
     **/
    String getFaultTimeByResourcesId(@Param("resourcesId") String resourcesId);

    /**
     * [通过商品id查询未被分配且未被锁定的资源]
     * @author 陈湘岳 2025/9/28
     * @param commodityId 商品id
     * @return com.ruoyi.system.domain.entity.ServerResources
     **/
    ServerResources findByCommodityId(@Param("commodityId") String commodityId);

    /**
     * [通过商品id查询未被分配资源集合]
     * @author 陈湘岳 2025/9/28
     * @param commodityId 商品id
     * @return com.ruoyi.system.domain.entity.ServerResources
     **/
    List<ServerResources> findByCommodityIdList(@Param("commodityId") String commodityId);

    /**
     * [通过资源id查询未被分配资源集合]
     * @author 陈湘岳 2025/9/28
     * @param id 商品id
     * @return com.ruoyi.system.domain.entity.ServerResources
     **/
    ServerResources findByServerResourcesIdForUpdate(@Param("id") String id);

    /**
     * [续费时，通过订单资源表查询对应的资源]
     * @author 陈湘岳 2025/10/29
     * @param id 订单id
     * @return com.ruoyi.system.domain.entity.ServerResources
     **/
    ServerResources findByOrderIdForUpdate(@Param("id") String id);


    /**
     * [查询新购资源订单]
     * @author 陈湘岳 2025/11/21
     * @param orderId 订单id
     * @return com.ruoyi.system.domain.vo.OrderNewVo
     **/
    OrderNewVo getOrderAdd(@Param("orderId") String orderId);

    /**
     * [查询续费资源订单]
     * @author 陈湘岳 2025/11/21
     * @param orderId 订单id
     * @return com.ruoyi.system.domain.vo.OrderNewVo
     **/
    OrderRenewalVo getOrderRenewal(@Param("orderId") String orderId);


    /**
     * [查询所有资源]
     * @author 陈湘岳 2025/11/21
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    List<ServerResources> findAll();

    /**
     * [查询所有正常资源]
     * @author 陈湘岳 2025/11/21
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    List<ServerResources> findNormalAll();

    /**
     * [根据资源状态查询资源列表]
     * @author 陈湘岳 2026/3/30
     * @param resourcesStatus 资源状态
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    List<ServerResources> findByResourcesStatus(@Param("resourcesStatus") String resourcesStatus);

    /**
     * [查询商品，上锁]
     * @author 陈湘岳 2026/3/21
     * @param resourcesId 资源id
     * @return com.ruoyi.system.domain.entity.ServerResources
     **/
    ServerResources selectByIdForUpdate(@Param("resourcesId") String resourcesId);

    /**
     * [查询到期资源]
     * 查询租赁到期时间小于当前时间减去30分钟的资源
     * @author 陈湘岳 2026/3/23
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    List<ServerResources> selectExpiredResources();

    /**
     * [查询3天内到期且状态为WAIT_NOTIFY的资源]
     * @author 陈湘岳 2026/4/11
     * @param limit 限制查询数量
     * @return java.util.List<com.ruoyi.system.domain.entity.ServerResources>
     **/
    List<ServerResources> findResourcesExpireInThreeDays(@Param("limit") int limit);

    /**
     * [根据资源id更新通知状态(status字段)]
     * @author 陈湘岳 2026/4/12
     * @param resourcesId 资源id
     * @param status 目标状态
     * @return int 影响行数
     **/
    int updateNotifyStatusById(@Param("resourcesId") String resourcesId, @Param("status") String status);

    /**
     * [查询全部资源数量]
     * @author 陈湘岳 2026/4/6
     * @return java.lang.Long
     **/
    Long countTotal();

    /**
     * [查询已售出资源数量]
     * @author 陈湘岳 2026/4/6
     * @return java.lang.Long
     **/
    Long countSold();
}
