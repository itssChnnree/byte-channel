package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.vo.ShopNoticeFileVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ShopNoticeFile;

import java.util.List;

/**
 * 公告附件表(ShopNoticeFile
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-23 22:38:29
 */
@Mapper
@Repository
public interface ShopNoticeFileMapper extends BaseMapper<ShopNoticeFile> {

    /**
     * [批量插入]
     * @author 陈湘岳 2025/11/23
     * @param list 插入
     * @return int
     **/
    int insertBatch(@Param("entities") List<ShopNoticeFile>  list);


    /**
     * [删除公告文件]
     * @author 陈湘岳 2025/11/24
     * @param shopNoticeId 公告id
     * @return int
     **/
    int deleteByShopNoticeId(@Param("shopNoticeId") String shopNoticeId);


    /**
     * [通过公告查询]
     * @author 陈湘岳 2025/11/24
     * @param shopNoticeId 公告id
     * @return java.util.List<com.ruoyi.system.domain.vo.ShopNoticeFileVo>
     **/
    List<ShopNoticeFileVo> selectList(@Param("shopNoticeId") String shopNoticeId);
}
