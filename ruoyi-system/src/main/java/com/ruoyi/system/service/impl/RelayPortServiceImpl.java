package com.ruoyi.system.service.impl;

import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.system.domain.dto.RelayPortDto;
import com.ruoyi.system.domain.entity.RelayPort;
import com.ruoyi.system.domain.vo.RelayPortPageVo;
import com.ruoyi.system.domain.vo.RelayPortVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.RelayPortMapper;
import com.ruoyi.system.mapstruct.RelayPortMapstruct;
import com.ruoyi.system.service.IRelayPortService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 中转端口表(RelayPort)服务实现
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-05-10
 */
@Service("relayPortService")
public class RelayPortServiceImpl implements IRelayPortService {

    @Resource
    private RelayPortMapper relayPortMapper;

    @Resource
    private RelayPortMapstruct relayPortMapstruct;

    /**
     * 新增中转端口配置
     */
    @Override
    @Transactional
    public Result insert(RelayPortDto dto) {
        if (StrUtil.isBlank(dto.getRelayResourceId())) {
            return Result.fail("所属中转资源不能为空");
        }
        RelayPort entity = relayPortMapstruct.changeDto2(dto);
        int insert = relayPortMapper.insert(entity);
        if (insert > 0) {
            return Result.success(entity);
        }
        return Result.fail("新增失败");
    }

    /**
     * 编辑中转端口配置
     */
    @Override
    @Transactional
    public Result update(RelayPortDto dto) {
        if (StrUtil.isBlank(dto.getId())) {
            return Result.fail("端口ID不能为空");
        }
        RelayPort entity = relayPortMapstruct.changeDto2(dto);
        int i = relayPortMapper.updateById(entity);
        if (i > 0) {
            return Result.success(entity);
        }
        return Result.fail("修改失败");
    }

    /**
     * 逻辑删除中转端口（设置is_deleted=1）
     */
    @Override
    @Transactional
    public Result delete(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("端口ID不能为空");
        }
        RelayPort entity = new RelayPort();
        entity.setId(id);
        entity.setIsDeleted(1);
        int i = relayPortMapper.updateById(entity);
        if (i > 0) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * 根据ID查询端口详情
     */
    @Override
    public Result findById(String id) {
        if (StrUtil.isBlank(id)) {
            return Result.fail("端口ID不能为空");
        }
        RelayPortVo vo = relayPortMapper.queryById(id);
        if (vo == null) {
            return Result.fail("端口不存在");
        }
        return Result.success(toPageVo(vo));
    }

    /**
     * 分页查询端口列表（LEFT JOIN relay_resource + relay_node）
     * 支持按端口号模糊搜索（keyword）、按节点筛选（nodeId）、按资源IP筛选（resourceIp）、按协议筛选（protocol）
     */
    @Override
    public Result page(RelayPortDto dto) {
        PageHelper.startPage(dto);
        List<RelayPortVo> list = relayPortMapper.queryPage(dto);
        List<RelayPortPageVo> voList = new ArrayList<>();
        for (RelayPortVo vo : list) {
            voList.add(toPageVo(vo));
        }
        return Result.success(new PageInfo<>(voList));
    }

    /**
     * 将RelayPortVo转换为RelayPortPageVo
     */
    private RelayPortPageVo toPageVo(RelayPortVo vo) {
        RelayPortPageVo pageVo = new RelayPortPageVo();
        pageVo.setId(vo.getId());
        pageVo.setRelayResourceId(vo.getRelayResourceId());
        pageVo.setTargetResourceId(vo.getTargetResourceId());
        pageVo.setPort(vo.getPort());
        pageVo.setProtocol(vo.getProtocol());
        pageVo.setTransport(vo.getTransport());
        pageVo.setPath(vo.getPath());
        pageVo.setSecurity(vo.getSecurity());
        pageVo.setPublicKey(vo.getPublicKey());
        pageVo.setSni(vo.getSni());
        pageVo.setShortId(vo.getShortId());
        pageVo.setNodePort(vo.getNodePort());
        pageVo.setNodeName(vo.getNodeName());
        pageVo.setResourceIp(vo.getResourceIp());
        pageVo.setCreateTime(vo.getCreateTime());
        return pageVo;
    }
}
