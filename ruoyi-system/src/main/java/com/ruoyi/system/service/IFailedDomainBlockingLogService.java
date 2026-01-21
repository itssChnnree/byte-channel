package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.FailedDomainBlockingLogDto;
import com.ruoyi.system.domain.entity.FailedDomainBlockingLog;
import com.ruoyi.system.domain.vo.FailedDomainBlockingLogVo;
import com.ruoyi.system.http.Result;


/**
 * 域名屏蔽未成功记录表(FailedDomainBlockingLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 13:40:06
 */
public interface IFailedDomainBlockingLogService {


    /**
     * [分页查询未成功记录]
     * @author 陈湘岳 2025/12/27
     * @param pageNum
     * @param pageSize
     * @return com.ruoyi.system.http.Result
     **/
    Result page(Integer pageNum, Integer pageSize,String ip , String commodityId, String categoryId);

    /**
     * [根据id变更处理状态]
     * @author 陈湘岳 2025/12/28
     * @param id  id
     * @return com.ruoyi.system.http.Result
     **/
    Result handle(String id);
}
