package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.dto.ShopNoticeDto;
import com.ruoyi.system.domain.vo.ShopNoticeVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ShopNotice;

import java.util.List;

/**
 * 商城公告表(ShopNotice
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:53:07
 */
@Mapper
@Repository
public interface ShopNoticeMapper extends BaseMapper<ShopNotice> {



    /**
     * [查询列表]
     *
     * @param shopNoticeDto 查询参数
     * @return java.util.List<com.ruoyi.system.domain.vo.ShopNoticeVo>
     * @author chenxiangyue 2025/11/23
     **/
    List<ShopNoticeVo> selectList(@Param("dto") ShopNoticeDto shopNoticeDto);

    /**
     * [用户查询列表]
     *
     * @param shopNoticeDto 查询参数
     * @return java.util.List<com.ruoyi.system.domain.vo.ShopNoticeVo>
     * @author chenxiangyue 2025/11/23
     **/
    List<ShopNoticeVo> userlist(@Param("dto") ShopNoticeDto shopNoticeDto);

    /**
     * [查询公告标题列表-只查询前五条]
     * @author 陈湘岳 2025/11/23
     * @param
     * @return java.util.List<com.ruoyi.system.domain.vo.ShopNoticeVo>
     **/
    List<ShopNoticeVo> titleList();

    /**
     * [用户查询公告详情]
     * @author 陈湘岳 2025/11/23
     * @param id
     * @return com.ruoyi.system.domain.vo.ShopNoticeVo
     **/
    ShopNoticeVo queryById(@Param("id") String id);
}
