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


    /**
     * 系统查询公告详情]
     * @author 陈湘岳 2025/11/27
     * @param id 公告id
     * @return com.ruoyi.system.domain.vo.ShopNoticeVo
     **/
    ShopNoticeVo getByIdSystem(String id);

    /**
     * [查询教程列表-复合游标分页]
     * 用于用户教程查询，支持sort+id复合游标避免重复数据问题
     * @author 陈湘岳 2026/3/23
     * @param lastSort 上次查询的sort值
     * @param lastId 上次查询的id
     * @param limit 查询数量
     * @return java.util.List<com.ruoyi.system.domain.vo.ShopNoticeVo>
     **/
    List<ShopNoticeVo> selectTutorialListByCursor(@Param("lastSort") Integer lastSort,
                                                   @Param("lastId") String lastId,
                                                   @Param("limit") int limit);
}
