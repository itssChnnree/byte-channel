package com.ruoyi.system.service;


import com.ruoyi.system.domain.base.LogEntry;
import com.ruoyi.system.domain.dto.log.SysUserOperLogDto;
import com.ruoyi.system.http.Result;

/**
 * @author a1152
 */
public interface IUserOperLogService {

    /**
     * [通过es查询用户操作日志]
     * @author 陈湘岳 2024/2/23
     * @param sysUserOperLogDTO 查询入参
     * @return com.edu.educenter.core.common.entity.CommonResult 查询结果
     **/
    Result getDateHistogram(SysUserOperLogDto sysUserOperLogDTO);


    /**
     * [日志保存]
     * @author 陈湘岳 2026/2/3
     * @param logEntry 日志
     * @return void
     **/
    void save(LogEntry logEntry);
}
