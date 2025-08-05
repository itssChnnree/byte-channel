package com.ruoyi.system.service;


import com.ruoyi.system.domain.dto.FaultHandlingDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.http.Result;

/**
 * 故障处理流程表(FaultHandling)�����ӿ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:54
 */
public interface IFaultHandlingService {


    /**
     * [新增故障处理流程]
     * @author 陈湘岳 2025/8/1
     * @param faultHandlingDto
     * @return com.ruoyi.system.http.Result
     **/
    Result insert(FaultHandlingDto faultHandlingDto);


    /**
     * [修改故障处理流程]
     * @author 陈湘岳 2025/8/2
     * @param faultHandlingDto
     * @return com.ruoyi.system.http.Result
     **/
    Result update(FaultHandlingDto faultHandlingDto);

    /**
     * [删除故障处理流程]
     * @author 陈湘岳 2025/8/3
     * @param listDto
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteByIds(ListDto listDto);

    /**
     * [分页查询故障处理流程]
     * @author 陈湘岳 2025/8/5
     * @param faultHandlingDto
     * @return com.ruoyi.system.http.Result
     **/
    Result pageQuery(FaultHandlingDto faultHandlingDto);
}
