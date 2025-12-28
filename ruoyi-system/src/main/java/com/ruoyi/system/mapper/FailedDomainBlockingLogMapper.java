package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.vo.FailedDomainBlockingLogVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.FailedDomainBlockingLog;

import java.util.List;

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


    /**
     * [分页查询]
     * @author 陈湘岳 2025/12/27
     * @param status 状态
     * @return java.util.List<com.ruoyi.system.domain.vo.FailedDomainBlockingLogVo>
     **/
    List<FailedDomainBlockingLogVo> pageList(@Param("status")String status);


    /**
     * [通过id变更处理状态]
     * @author 陈湘岳 2025/12/28
     * @param id id
     * @param status 状态
     * @return int
     **/
    int updateStatus(@Param("id") String id, @Param("status") String status);
}
