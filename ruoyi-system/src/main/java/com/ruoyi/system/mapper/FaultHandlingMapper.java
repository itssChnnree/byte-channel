package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.FaultHandling;
import com.ruoyi.system.domain.dto.FaultHandlingDto;
import com.ruoyi.system.domain.vo.FaultHandlingVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


/**
 * 故障处理流程表(FaultHandling)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:23:54
 */
@Mapper
@Repository
public interface FaultHandlingMapper extends BaseMapper<FaultHandling> {



    /**
     * [修改故障处理流程]
     * @author 陈湘岳 2025/8/2
     * @param faultHandling
     * @return int
     **/
    int update(FaultHandling faultHandling);


    /**
     * [根据主键id及用户id删除故障申报]
     * @author 陈湘岳 2025/8/3
     * @param idList 主键id集合
     * @param userId 用户id
     * @return int
     **/
    int deleteByIdAndUserId(@Param("list") List<String> idList,@Param("userId") String userId);

    /**
     * [分页查询故障处理流程]
     * @author 陈湘岳 2025/8/5
     * @param faultHandling 查询参数
     * @return java.util.List<com.ruoyi.system.domain.FaultHandling>
     **/
    List<FaultHandlingVo> queryList(FaultHandlingDto faultHandling);
}
