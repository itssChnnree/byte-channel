package com.ruoyi.system.mapper;


import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.TransactionChangeLog;

/**
 * 流水线变更表(TransactionChangeLog
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-02-23 17:47:48
 */
@Mapper
@Repository
public interface TransactionChangeLogMapper extends BaseMapper<TransactionChangeLog> {

}
