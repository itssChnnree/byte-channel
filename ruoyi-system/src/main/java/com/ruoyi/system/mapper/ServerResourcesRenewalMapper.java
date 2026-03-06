package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ServerResourcesRenewal;

/**
 * 服务器资源表(ServerResourcesRenewal)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-28 10:48:26
 */
@Mapper
@Repository
public interface ServerResourcesRenewalMapper extends BaseMapper<ServerResourcesRenewal> {


    /**
     * [根据用户id及资源id查询续费配置]
     * @author 陈湘岳 2026/3/5
     * @param userId 用户id
     * @param resourcesId 资源id
     * @return com.ruoyi.system.domain.entity.ServerResourcesRenewal
     **/
    ServerResourcesRenewal findByUserIdAndResourcesId(@Param("userId") String userId,
                                                      @Param("resourcesId") String resourcesId);

    /**
     * [根据用户id和资源id删除续费配置]
     * @author 陈湘岳 2026/3/5
     * @param resourcesId 资源id
     * @return int
     **/
    int deleteByResourcesId(@Param("resourcesId") String resourcesId);
}
