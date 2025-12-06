package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.ShopNoticeDto;
import com.ruoyi.system.domain.entity.ShopNotice;
import com.ruoyi.system.domain.vo.ShopNoticeVo;
import com.ruoyi.system.http.Result;

import javax.validation.Valid;


/**
 * 商城公告表(ShopNotice)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:28
 */
public interface IShopNoticeService {


    /**
     * [新增公告]
     * @author 陈湘岳 2025/11/23
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     **/
    Result add(@Valid ShopNoticeDto shopNoticeDto);

    /**
     * [分页查询]
     * @author 陈湘岳 2025/11/23
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     **/
    Result list(ShopNoticeDto shopNoticeDto);

    /**
     * [分页查询]
     * @author 陈湘岳 2025/11/23
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     **/
    Result userlist(ShopNoticeDto shopNoticeDto);


    /**
     * [查询公告标题列表]
     * @author 陈湘岳 2025/11/23
     * @param
     * @return com.ruoyi.system.http.Result
     **/
    Result titleList();

    /**
     * [根据公告id删除公告]
     * @author 陈湘岳 2025/11/24
     * @param id 公告id
     * @return com.ruoyi.system.http.Result
     **/
    Result deleteById(String id);

    /**
     * [根据公告id查询公告]
     * @author 陈湘岳 2025/11/24
     * @param id 公告id
     * @return com.ruoyi.system.http.Result
     **/
    Result<ShopNoticeVo> getById(String id);


    /**
     * [更新公告]
     * @author 陈湘岳 2025/11/24
     * @param shopNoticeDto
     * @return com.ruoyi.system.http.Result
     **/
    Result update( ShopNoticeDto shopNoticeDto);

    /**
     * [后台查询公告详情]
     * @author 陈湘岳 2025/11/27
     * @param id 公告id
     * @return com.ruoyi.system.http.Result<com.ruoyi.system.domain.vo.ShopNoticeVo>
     **/
    Result<ShopNoticeVo> getByIdSystem(String id);

    /**
     * [变更公告状态]
     * @author 陈湘岳 2025/11/27
     * @param id 公告id
     * @return com.ruoyi.system.http.Result
     **/
    Result changeStatus(String id);
}
