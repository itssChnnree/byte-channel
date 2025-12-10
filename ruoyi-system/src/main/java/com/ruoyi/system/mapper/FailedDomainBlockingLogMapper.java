package com.ruoyi.system.mapper;


import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.FailedDomainBlockingLog;

/**
 * 域名屏蔽未成功记录表(FailedDomainBlockingLog
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 21:47:48
 */
@Mapper
@Repository
public interface FailedDomainBlockingLogMapper extends BaseMapper<FailedDomainBlockingLog> {

}
