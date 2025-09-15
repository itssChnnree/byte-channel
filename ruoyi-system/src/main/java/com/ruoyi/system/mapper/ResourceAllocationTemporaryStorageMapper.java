package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.vo.ResourceAllocationTemporaryStorageVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ResourceAllocationTemporaryStorage;

import java.util.List;

/**
 * 资源暂存表(ResourceAllocationTemporaryStorage)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-11 15:28:59
 */
@Mapper
@Repository
public interface ResourceAllocationTemporaryStorageMapper extends BaseMapper<ResourceAllocationTemporaryStorage> {



    /**
     * [查询创建时间最久的40条数据]
     * @author 陈湘岳 2025/9/12
     * @param
     * @return java.util.List<com.ruoyi.system.domain.vo.ResourceAllocationTemporaryStorageVo>
     **/
    List<ResourceAllocationTemporaryStorageVo> page(@Param("ip") String ip);
}
