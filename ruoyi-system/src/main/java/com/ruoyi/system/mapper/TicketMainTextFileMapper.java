package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.vo.TicketMainTextFileVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.TicketMainTextFile;

import java.util.List;

/**
 * 工单正文文件附件表(TicketMainTextFile)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-09-29 14:57:37
 */
@Mapper
@Repository
public interface TicketMainTextFileMapper extends BaseMapper<TicketMainTextFile> {

    /**
     * 批量新增数据（MyBatis原生foreach方法）
     *
     * @param list 实例对象列表
     * @return 影响行数
     */
    int insertBatch(@Param("entities") List<TicketMainTextFile> list);

    /**
     * [通过工单正文查询文件]
     * @author 陈湘岳 2025/10/13
     * @param ticketMainTextIds 工单正文id集合
     * @return java.util.List<com.ruoyi.system.domain.vo.TicketMainTextFileVo>
     **/
    List<TicketMainTextFileVo> findByTicketMainTextId(@Param("list") List<String> ticketMainTextIds);
}
