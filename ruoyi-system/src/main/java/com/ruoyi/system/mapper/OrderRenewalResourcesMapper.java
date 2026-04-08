package com.ruoyi.system.mapper;




import com.ruoyi.system.domain.vo.OrderRenewalResourcesVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.OrderRenewalResources;

/**
 * 订单-续费资源表(OrderRenewalResources)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-15 23:50:17
 */
@Mapper
@Repository
public interface OrderRenewalResourcesMapper extends BaseMapper<OrderRenewalResources> {


    /**
     * [根据订单id查询订单续费记录]
     * @author 陈湘岳 2026/1/20
     * @param orderId 订单id
     * @return com.ruoyi.system.domain.entity.OrderRenewalResources
     **/
    OrderRenewalResourcesVo findByOrderId(String orderId);

    /**
     * [将老的资源id替换为新的]
     * @author 陈湘岳 2026/4/7
     * @param oldId 老的资源id
     * @param newId 新的资源id
     * @param userId 用户id
     * @return int
     **/
    int resourceReplacement(@Param("oldResourcesId")String oldId,
                            @Param("newResourcesId")String newId,
                            @Param("newIp")String newIp,
                            @Param("userId")String userId);
}
