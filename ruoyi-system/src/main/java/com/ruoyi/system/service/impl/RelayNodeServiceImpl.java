package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.dto.RelayNodeDto;
import com.ruoyi.system.domain.entity.RelayNode;
import com.ruoyi.system.domain.vo.RelayNodePageVo;
import com.ruoyi.system.domain.vo.RelayNodeVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.RelayNodeMapper;
import com.ruoyi.system.mapstruct.RelayNodeMapstruct;
import com.ruoyi.system.service.IRelayNodeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * 中转节点表(RelayNode)服务实现
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Service("relayNodeService")
public class RelayNodeServiceImpl implements IRelayNodeService {

    @Resource
    private RelayNodeMapper relayNodeMapper;

    @Resource
    private RelayNodeMapstruct relayNodeMapstruct;

    /**
     * 新增中转节点
     */
    @Override
    @Transactional
    public Result insert(RelayNodeDto dto) {
        if (StrUtil.isBlank(dto.getNodeName())) {
            return Result.fail("节点名称不能为空");
        }
        RelayNode entity = relayNodeMapstruct.changeDto2(dto);
        entity.setAvailableStatus(1);
        int insert = relayNodeMapper.insert(entity);
        if (insert > 0) {
            return Result.success(entity);
        }
        return Result.fail("新增失败");
    }

    /**
     * 编辑中转节点
     */
    @Override
    @Transactional
    public Result update(RelayNodeDto dto) {
        if (StrUtil.isBlank(dto.getId())) {
            return Result.fail("节点ID不能为空");
        }
        RelayNode entity = relayNodeMapstruct.changeDto2(dto);
        int i = relayNodeMapper.updateById(entity);
        if (i > 0) {
            return Result.success(entity);
        }
        return Result.fail("修改失败");
    }

    /**
     * 逻辑删除中转节点（设置is_deleted=1）
     */
    @Override
    @Transactional
    public Result delete(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("节点ID不能为空");
        }
        RelayNode entity = new RelayNode();
        entity.setId(id);
        entity.setIsDeleted(1);
        int i = relayNodeMapper.updateById(entity);
        if (i > 0) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据ID查询节点详情
     */
    @Override
    public Result findById(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("节点ID不能为空");
        }
        RelayNodeVo vo = relayNodeMapper.queryById(id);
        if (vo == null) {
            return Result.fail("节点不存在");
        }
        return Result.success(toPageVo(vo));
    }

    /**
     * 分页查询节点列表（含资源数量）
     * 直接通过SQL子查询返回RelayNodePageVo，无需循环转换
     */
    @Override
    public Result page(RelayNodeDto dto) {
        PageHelper.startPage(dto);
        List<RelayNodePageVo> list = relayNodeMapper.queryPageVo(dto);
        return Result.success(new PageInfo<>(list));
    }

    /**
     * 查询全部未删除节点（供下拉选择，含资源数量）
     */
    @Override
    public Result list() {
        List<RelayNodePageVo> list = relayNodeMapper.queryListVo();
        return Result.success(list);
    }

    /**
     * 切换节点启用/停用状态
     * 通过一条SQL的CASE WHEN实现原子翻转，无需查询当前状态
     */
    @Override
    @Transactional
    public Result toggleStatus(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("节点ID不能为空");
        }
        int i = relayNodeMapper.toggleAvailableStatus(id);
        if (i > 0) {
            return Result.success("操作成功");
        }
        return Result.fail("操作失败");
    }

    /**
     * 将RelayNodeVo转换为RelayNodePageVo
     */
    private RelayNodePageVo toPageVo(RelayNodeVo vo) {
        RelayNodePageVo pageVo = new RelayNodePageVo();
        pageVo.setId(vo.getId());
        pageVo.setNodeName(vo.getNodeName());
        pageVo.setDescription(vo.getDescription());
        pageVo.setAvailableStatus(vo.getAvailableStatus());
        pageVo.setCreateTime(vo.getCreateTime());
        return pageVo;
    }
}
