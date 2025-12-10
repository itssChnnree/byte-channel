package com.ruoyi.system.mapper;


import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ScheduledDomainLockingTime;

/**
 * 域名屏蔽重启节点预约时间(ScheduledDomainLockingTime
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 14:13:39
 */
@Mapper
@Repository
public interface ScheduledDomainLockingTimeMapper extends BaseMapper<ScheduledDomainLockingTime> {


    /**
     * [查询距今最近的预约时间]
     *
     * @return java.util.List<com.ruoyi.system.domain.entity.ScheduledDomainLockingTime>
     * @author 陈湘岳 2025/12/8
     **/
    ScheduledDomainLockingTime findMinScheduledTime(@Param("status")String  status);

}
