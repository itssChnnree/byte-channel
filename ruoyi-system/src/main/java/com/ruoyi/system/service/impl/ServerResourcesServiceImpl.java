package com.ruoyi.system.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ruoyi.system.constant.ResourcesStatus;
import com.ruoyi.system.domain.entity.Commodity;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.dto.ServerResourcesDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.CommodityMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapstruct.ServerResourcesMapstruct;
import com.ruoyi.system.service.IServerResourcesService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.annotation.Resource;

/**
 * 服务器资源表(ServerResources)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:25
 */
@Service("serverResourcesService")
public class ServerResourcesServiceImpl  implements IServerResourcesService {


    @Resource
    private ServerResourcesMapstruct serverResourcesMapstruct;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private CommodityMapper commodityMapper;


    /**
     * [新增服务器资源]
     *
     * @param serverResourcesDto 服务器资源
     * @return com.ruoyi.system.domain.entity.ServerResources
     * @author 陈湘岳 2025/7/23
     **/
    @Override
    public Result<ServerResources> insert(ServerResourcesDto serverResourcesDto) {
        ServerResources serverResources = serverResourcesMapstruct.changeDto2(serverResourcesDto);
        QueryWrapper<Commodity> commodityWrapper = new QueryWrapper<>();
        commodityWrapper.eq("id",serverResources.getCommodityId());
        commodityWrapper.eq("is_deleted",0);
        //校验所属商品是否存在
        Commodity commodity = commodityMapper.selectOne(commodityWrapper);
        if (ObjectUtils.isEmpty( commodity)){
            return Result.fail("所选商品不存在");
        }
        int insert = serverResourcesMapper.insert(serverResourcesBuild(serverResources));
        if (insert > 0){
            return Result.success(serverResources);
        } else {
            return Result.fail("新增失败");
        }
    }


    private ServerResources serverResourcesBuild(ServerResources serverResources){
        serverResources.setResourcesStatus(ResourcesStatus.WAIT_NOTICE_DATA);
        return serverResources;
    }
}
