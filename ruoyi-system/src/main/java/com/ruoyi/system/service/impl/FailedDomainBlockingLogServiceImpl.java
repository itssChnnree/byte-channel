package com.ruoyi.system.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.system.constant.EntityStatus;
import com.ruoyi.system.domain.dto.FailedDomainBlockingLogDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.FailedDomainBlockingLogMapper;
import com.ruoyi.system.service.IFailedDomainBlockingLogService;
import com.ruoyi.system.domain.entity.FailedDomainBlockingLog;
import com.ruoyi.system.domain.vo.FailedDomainBlockingLogVo;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.annotation.Resources;
import java.util.List;

/**
 * 域名屏蔽未成功记录表(FailedDomainBlockingLog)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-12-08 13:40:06
 */
@Service("failedDomainBlockingLogService")
public class FailedDomainBlockingLogServiceImpl implements IFailedDomainBlockingLogService {


    @Resource
    private FailedDomainBlockingLogMapper failedDomainBlockingLogMapper;


    /**
     * [分页查询未成功记录]
     *
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/12/27
     **/
    @Override
    public Result page(Integer pageNum, Integer pageSize,String ip , String commodityId, String categoryId) {
        PageUtils.startPage(pageNum, pageSize);
        List<FailedDomainBlockingLogVo> failedDomainBlockingLogVos = failedDomainBlockingLogMapper.pageList(EntityStatus.UNHANDLED, ip, commodityId, categoryId);
        return Result.success(new PageInfo<>(failedDomainBlockingLogVos));
    }


    /**
     * [根据id变更处理状态]
     *
     * @param id id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/12/28
     **/
    @Override
    @Transactional
    public Result handle(String id) {
        int i = failedDomainBlockingLogMapper.updateStatus(id, EntityStatus.HANDLED);
        LogEsUtil.info("屏蔽域名未成功处理记录，id："+id+",处理结果为："+i);
        return i > 0 ? Result.success() : Result.fail("修改失败");
    }
}
