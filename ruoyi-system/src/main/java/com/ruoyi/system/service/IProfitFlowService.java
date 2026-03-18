package com.ruoyi.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ruoyi.system.domain.dto.ProfitFlowDto;
import com.ruoyi.system.domain.entity.ProfitFlow;
import com.ruoyi.system.domain.vo.ProfitFlowVo;
import com.ruoyi.system.http.Result;

import java.math.BigDecimal;

/**
 * 利润流水表(ProfitFlow)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 00:12:43
 */
public interface IProfitFlowService {

    /**
     * [增加利润流水]
     *
     * @param profitFlow 利润流水实体
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    void add(ProfitFlow profitFlow);

    /**
     * [增加利润流水]
     *
     * @param profitFlowDto 利润流水实体
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    Result addForController(ProfitFlowDto profitFlowDto);

    /**
     * [删除利润流水]
     *
     * @param sourceType 业务类型
     * @param sourceId   业务ID
     * @author chenxiangyue 2026/3/15
     **/
    void deleteBySourceTypeAndId(String sourceType, String sourceId);

    /**
     * [分页查询流水列表]
     *
     * @param profitFlowDto 查询条件
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    Result page(ProfitFlowDto profitFlowDto);

    /**
     * [统计利润总和]
     *
     * @param profitFlowDto 查询条件
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    Result sumProfit(ProfitFlowDto profitFlowDto);

    /**
     * [获取利润卡片]
     *
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    Result getProfitCard();

    /**
     * [获取利润折线图]
     *
     * @param type 类型
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    Result getProfitLine(String type);
}
