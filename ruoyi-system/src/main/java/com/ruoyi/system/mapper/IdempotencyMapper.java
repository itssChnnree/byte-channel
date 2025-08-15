package com.ruoyi.system.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.Idempotency;

/**
 * 幂等性控制表(Idempotency)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-08-15 14:25:32
 */
@Mapper
@Repository
public interface IdempotencyMapper extends BaseMapper<Idempotency> {



    /**
     * [通过id及类型查询幂等信息]
     * @author 陈湘岳 2025/8/15
     * @param id id
     * @param type 类型
     * @return int
     **/
    int getByIdAndType(@Param("id")String id, @Param("type") String type);

}
