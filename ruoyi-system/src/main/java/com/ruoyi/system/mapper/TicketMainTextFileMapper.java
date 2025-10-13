package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
}
