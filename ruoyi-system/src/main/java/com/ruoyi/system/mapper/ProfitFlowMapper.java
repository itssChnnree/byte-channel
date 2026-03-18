package com.ruoyi.system.mapper;


import com.ruoyi.system.domain.dto.ProfitFlowDto;
import com.ruoyi.system.domain.vo.ProfitFlowLineChartVo;
import com.ruoyi.system.domain.vo.ProfitFlowVo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.ProfitFlow;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * 利润流水表(ProfitFlow
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 00:12:38
 */
@Mapper
@Repository
public interface ProfitFlowMapper extends BaseMapper<ProfitFlow> {


    /**
     * [分页查询]
     * @author 陈湘岳 2026/3/15
     * @param profitFlowDto 入参
     * @return java.util.List<com.ruoyi.system.domain.vo.ProfitFlowVo>
     **/
    List<ProfitFlowVo> selectPage(@Param("dto") ProfitFlowDto profitFlowDto);

    /**
     * [查询总和]
     * @author 陈湘岳 2026/3/15
     * @param profitFlowDto 入参
     * @return java.math.BigDecimal
     **/
    BigDecimal getSum(@Param("dto") ProfitFlowDto profitFlowDto);

    /**
     * [按天查询]
     * @author 陈湘岳 2026/3/15
     * @param date 入参
     * @return java.util.List<com.ruoyi.system.domain.vo.ProfitFlowLineChartVo>
     **/
    List<ProfitFlowLineChartVo> getByDay(@Param("date")Date date);

    /**
     * [按月查询]
     * @author 陈湘岳 2026/3/15
     * @param date 入参
     * @return java.util.List<com.ruoyi.system.domain.vo.ProfitFlowLineChartVo>
     **/
    List<ProfitFlowLineChartVo> getByMonth(@Param("date")Date date);

}
