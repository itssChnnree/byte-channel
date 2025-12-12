package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.ResourceBlockDomainDto;
import com.ruoyi.system.domain.entity.ResourceBlockDomain;
import com.ruoyi.system.domain.vo.ResourceBlockDomainVo;
import com.ruoyi.system.http.Result;

import java.util.Date;


/**
 * 资源屏蔽域名表(ResourceBlockDomain)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 13:40:07
 */
public interface IResourceBlockDomainService {


    /**
     * [新增]
     * @author 陈湘岳 2025/12/8
     * @param resourceBlockDomain
     * @return java.lang.String
     **/
    Result add(ResourceBlockDomainDto resourceBlockDomain);

    /**
     * [根据域名id删除屏蔽域名]
     * @author 陈湘岳 2025/12/10
     * @param id 域名id
     * @param scheduleTime 预约时间
     * @param isAddToRecentUpdatePlan 是否加入最近更新计划
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteById(String id, Date scheduleTime, Boolean isAddToRecentUpdatePlan);


    /**
     * [查询列表]
     * @author 陈湘岳 2025/12/10
     * @param resourceBlockDomainDto
     * @return com.ruoyi.system.http.Result
     **/
    Result list(ResourceBlockDomainDto resourceBlockDomainDto);

    /**
     * [根据id更新屏蔽域名]
     * @author 陈湘岳 2025/12/11
     * @param resourceBlockDomainDto 更新入参
     * @return com.ruoyi.system.http.Result
     **/
    Result update(ResourceBlockDomainDto resourceBlockDomainDto);
}
