package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.dto.RelayResourceDto;
import com.ruoyi.system.domain.entity.RelayResource;
import com.ruoyi.system.domain.vo.RelayResourcePageVo;
import com.ruoyi.system.domain.vo.RelayResourceVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.RelayResourceMapper;
import com.ruoyi.system.mapstruct.RelayResourceMapstruct;
import com.ruoyi.system.service.IRelayResourceService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 中转资源表(RelayResource)服务实现
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Service("relayResourceService")
public class RelayResourceServiceImpl implements IRelayResourceService {

    @Resource
    private RelayResourceMapper relayResourceMapper;

    @Resource
    private RelayResourceMapstruct relayResourceMapstruct;

    /**
     * 新增中转资源
     * 默认启用状态为1（启用）
     */
    @Override
    @Transactional
    public Result insert(RelayResourceDto dto) {
        if (StrUtil.isBlank(dto.getNodeId())) {
            return Result.fail("所属节点不能为空");
        }
        if (StrUtil.isBlank(dto.getResourceIp())) {
            return Result.fail("VPS IP不能为空");
        }
        RelayResource entity = relayResourceMapstruct.changeDto2(dto);
        entity.setAvailableStatus(1);
        int insert = relayResourceMapper.insert(entity);
        if (insert > 0) {
            return Result.success(entity);
        }
        return Result.fail("新增失败");
    }

    /**
     * 编辑中转资源
     */
    @Override
    @Transactional
    public Result update(RelayResourceDto dto) {
        if (StrUtil.isBlank(dto.getId())) {
            return Result.fail("资源ID不能为空");
        }
        RelayResource entity = relayResourceMapstruct.changeDto2(dto);
        int i = relayResourceMapper.updateById(entity);
        if (i > 0) {
            return Result.success(entity);
        }
        return Result.fail("修改失败");
    }

    /**
     * 逻辑删除中转资源（设置is_deleted=1）
     */
    @Override
    @Transactional
    public Result delete(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("资源ID不能为空");
        }
        RelayResource entity = new RelayResource();
        entity.setId(id);
        entity.setIsDeleted(1);
        int i = relayResourceMapper.updateById(entity);
        if (i > 0) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据ID查询资源详情
     */
    @Override
    public Result findById(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("资源ID不能为空");
        }
        RelayResourceVo vo = relayResourceMapper.queryById(id);
        if (vo == null) {
            return Result.fail("资源不存在");
        }
        return Result.success(toPageVo(vo));
    }

    /**
     * 分页查询资源列表（LEFT JOIN relay_node获取节点名称）
     * 支持按IP模糊搜索（resourceIpLike）和按节点筛选（nodeId）
     */
    @Override
    public Result page(RelayResourceDto dto) {
        PageHelper.startPage(dto);
        List<RelayResourceVo> list = relayResourceMapper.queryPage(dto);
        List<RelayResourcePageVo> voList = new ArrayList<>();
        for (RelayResourceVo vo : list) {
            voList.add(toPageVo(vo));
        }
        return Result.success(new PageInfo<>(voList));
    }

    /**
     * 查询全部未删除资源（供下拉选择）
     */
    @Override
    public Result list() {
        List<RelayResourceVo> list = relayResourceMapper.queryList(new RelayResourceDto());
        List<RelayResourcePageVo> voList = new ArrayList<>();
        for (RelayResourceVo vo : list) {
            voList.add(toPageVo(vo));
        }
        return Result.success(voList);
    }

    /**
     * 切换资源启用/停用状态
     * 通过一条SQL的CASE WHEN实现原子翻转，无需查询当前状态
     */
    @Override
    @Transactional
    public Result toggleStatus(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("资源ID不能为空");
        }
        int i = relayResourceMapper.toggleAvailableStatus(id);
        if (i > 0) {
            return Result.success("操作成功");
        }
        return Result.fail("操作失败");
    }

    /**
     * 将RelayResourceVo转换为RelayResourcePageVo
     */
    private RelayResourcePageVo toPageVo(RelayResourceVo vo) {
        RelayResourcePageVo pageVo = new RelayResourcePageVo();
        pageVo.setId(vo.getId());
        pageVo.setNodeId(vo.getNodeId());
        pageVo.setNodeName(vo.getNodeName());
        pageVo.setVendorAccountId(vo.getVendorAccountId());
        pageVo.setResourceIp(vo.getResourceIp());
        pageVo.setResourcePort(vo.getResourcePort());
        pageVo.setResourceUserName(vo.getResourceUserName());
        pageVo.setTransferableQuantity(vo.getTransferableQuantity());
        pageVo.setAvailableStatus(vo.getAvailableStatus());
        pageVo.setCreateTime(vo.getCreateTime());
        return pageVo;
    }
}
