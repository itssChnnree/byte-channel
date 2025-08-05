package com.ruoyi.system.service.impl;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.FaultHandingStatus;
import com.ruoyi.system.domain.FaultHandling;
import com.ruoyi.system.domain.ServerResources;
import com.ruoyi.system.domain.dto.FaultHandlingDto;
import com.ruoyi.system.domain.dto.ListDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.FaultHandlingMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapstruct.FaultHandlingMapstruct;
import com.ruoyi.system.service.IFaultHandlingService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * 故障处理流程表(FaultHandling)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:54
 */
@Service("faultHandlingService")
public class FaultHandlingServiceImpl implements IFaultHandlingService {


    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private FaultHandlingMapper faultHandlingMapper;

    @Resource
    private FaultHandlingMapstruct faultHandlingMapstruct;

    /**
     * [新增故障处理流程]
     *
     * @param faultHandlingDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/1
     **/
    @Override
    public Result insert(FaultHandlingDto faultHandlingDto) {
        ServerResources serverResources = serverResourcesMapper.selectById(faultHandlingDto.getResourcesId());
        if (serverResources == null){
            return Result.fail("资源不存在");
        }
        FaultHandling faultHandling = faultHandlingMapstruct.changeDto2(faultHandlingDto);
        faultHandling.setUserId(serverResources.getUserId());
        faultHandling.setStatus(FaultHandingStatus.WAIT_HANDLE);
        int insert = faultHandlingMapper.insert(faultHandling);
        return insert > 0 ? Result.success(faultHandling) : Result.fail("添加失败");
    }


    /**
     * [修改故障处理流程]
     *
     * @param faultHandlingDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/2
     **/
    @Override
    public Result update(FaultHandlingDto faultHandlingDto) {
        FaultHandling faultHandling = faultHandlingMapstruct.changeDto2(faultHandlingDto);
        int update = faultHandlingMapper.update(faultHandling);
        return update > 0 ? Result.success(faultHandling) : Result.fail("修改失败");
    }


    /**
     * [删除故障处理流程]
     *
     * @param listDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/3
     **/
    @Override
    public Result deleteByIds(ListDto listDto) {
        if (CollectionUtils.isEmpty(listDto.getIds())){
            return Result.fail("请选择要删除的故障处理流程");
        }
        String userId = SecurityUtils.getStrUserId();
        int delete = faultHandlingMapper.deleteByIdAndUserId(listDto.getIds(), userId);
        return delete > 0 ? Result.success("已删除") : Result.fail("删除失败");
    }


    /**
     * [分页查询故障处理流程]
     *
     * @param faultHandlingDto
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/5
     **/
    @Override
    public Result pageQuery(FaultHandlingDto faultHandlingDto) {
        PageHelper.startPage(faultHandlingDto);
        List<FaultHandling> faultHandlings = faultHandlingMapper.queryList(faultHandlingDto);
        return Result.success(new PageInfo<>(faultHandlings));
    }
}
