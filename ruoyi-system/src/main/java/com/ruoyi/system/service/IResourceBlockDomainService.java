package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.ResourceBlockDomainDto;
import com.ruoyi.system.domain.entity.ResourceBlockDomain;
import com.ruoyi.system.domain.vo.ResourceBlockDomainVo;
import com.ruoyi.system.http.Result;


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
}
