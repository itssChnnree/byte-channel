package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.exception.base.BaseException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import com.ruoyi.system.constant.OrderTypeConstant;
import com.ruoyi.system.domain.dto.ProfitFlowDto;
import com.ruoyi.system.domain.vo.ProfitCardVo;
import com.ruoyi.system.domain.vo.ProfitFlowLineChartVo;
import com.ruoyi.system.mapper.ProfitFlowMapper;
import com.ruoyi.system.mapstruct.ProfitFlowMapstruct;
import com.ruoyi.system.service.IProfitFlowService;
import com.ruoyi.system.domain.entity.ProfitFlow;
import com.ruoyi.system.domain.vo.ProfitFlowVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 利润流水表(ProfitFlow)
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 00:12:46
 */
@Service("profitFlowService")
public class ProfitFlowServiceImpl implements IProfitFlowService {

    @Resource
    private ProfitFlowMapper profitFlowMapper;

    @Resource
    private ProfitFlowMapstruct profitFlowMapstruct;

    /**
     * [增加利润流水]
     *
     * @param profitFlow 利润流水实体
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @Override
    public void add(ProfitFlow profitFlow) {
        // 参数校验
        if (ObjectUtil.isNull(profitFlow.getProfitAmount())) {
            LogEsUtil.warn("增加利润流水失败：利润金额不能为空");
            return;
        }
        if (ObjectUtil.isEmpty(profitFlow.getSourceType())) {
            LogEsUtil.warn("增加利润流水失败：来源类型不能为空");
            return;
        }
        int insert = profitFlowMapper.insert(profitFlow);
        if (insert > 0) {
            LogEsUtil.info("增加利润流水成功，id：" + profitFlow.getId());
        } else {
            LogEsUtil.warn("增加利润流水失败");
            throw new BaseException("增加利润流水失败");
        }
    }

    /**
     * [增加利润流水]
     *
     * @param profitFlowDto 利润流水实体
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @Override
    public Result addForController(ProfitFlowDto profitFlowDto) {
        ProfitFlow profitFlow = profitFlowMapstruct.changeDto2(profitFlowDto);
        profitFlow.setSourceType(OrderTypeConstant.MANUAL);
        int insert = profitFlowMapper.insert(profitFlow);
        if (insert > 0) {
            LogEsUtil.info("增加利润流水成功，id：" + profitFlow.getId());
        } else {
            LogEsUtil.warn("增加利润流水失败");
            return Result.fail("增加利润流水失败");
        }
        return Result.success(profitFlow);
    }

    /**
     * [删除利润流水]
     *
     * @param sourceType 业务类型
     * @param sourceId   业务ID
     * @author chenxiangyue 2026/3/15
     **/
    @Override
    public void deleteBySourceTypeAndId(String sourceType, String sourceId) {
        // 参数校验
        if (ObjectUtil.isEmpty(sourceType)) {
            LogEsUtil.warn("删除利润流水失败：来源类型不能为空");
            return;
        }
        if (ObjectUtil.isNull(sourceId)) {
            LogEsUtil.warn("删除利润流水失败：来源ID不能为空");
            return;
        }
        LambdaQueryWrapper<ProfitFlow> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ProfitFlow::getSourceType, sourceType)
               .eq(ProfitFlow::getSourceId, sourceId);
        int delete = profitFlowMapper.delete(wrapper);
        if (delete > 0) {
            LogEsUtil.info("删除利润流水成功，sourceType：" + sourceType + "，sourceId：" + sourceId);
        } else {
            LogEsUtil.warn("删除利润流水失败，sourceType：" + sourceType + "，sourceId：" + sourceId);
        }
    }

    /**
     * [分页查询流水列表]
     *
     * @param profitFlowDto 查询条件
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @Override
    public Result page(ProfitFlowDto profitFlowDto) {
        // 构建查询条件
        PageHelper.startPage(profitFlowDto);
        List<ProfitFlowVo> page = profitFlowMapper.selectPage(profitFlowDto);
        return Result.success(new PageInfo<>(page));
    }

    /**
     * [统计利润总和]
     *
     * @param profitFlowDto 查询条件
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @Override
    public Result sumProfit(ProfitFlowDto profitFlowDto) {
        BigDecimal sum = profitFlowMapper.getSum(profitFlowDto);
        return Result.success(sum);
    }


    /**
     * [获取利润卡片]
     *
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @Override
    public Result getProfitCard() {
        ProfitCardVo profitCardVo = new ProfitCardVo();

        CompletableFuture<Void> dayCompletableFuture = CompletableFuture.runAsync(() -> {
            Date date = DateUtils.beginOfDay();
            ProfitFlowDto profitFlowDto = new ProfitFlowDto();
            profitFlowDto.setStartTime(date);
            BigDecimal sum = profitFlowMapper.getSum(profitFlowDto);
            profitCardVo.setDayProfit(sum);
        });

        CompletableFuture<Void> monthCompletableFuture = CompletableFuture.runAsync(() -> {
            Date date = DateUtils.beginOfMonth();
            ProfitFlowDto profitFlowDto = new ProfitFlowDto();
            profitFlowDto.setStartTime(date);
            BigDecimal sum = profitFlowMapper.getSum(profitFlowDto);
            profitCardVo.setMonthProfit(sum);

        });

        CompletableFuture<Void> yearCompletableFuture = CompletableFuture.runAsync(() -> {
            Date date = DateUtils.beginOfYear();
            ProfitFlowDto profitFlowDto = new ProfitFlowDto();
            profitFlowDto.setStartTime(date);
            BigDecimal sum = profitFlowMapper.getSum(profitFlowDto);
            profitCardVo.setYearProfit(sum);

        });

        CompletableFuture.allOf(dayCompletableFuture, monthCompletableFuture, yearCompletableFuture).join();
        return Result.success(profitCardVo);
    }

    /**
     * [获取利润折线图]
     *
     * @param type 类型
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @Override
    public Result getProfitLine(String type) {
        Date startTime = getStartTime(type);
        if(startTime == null){
            return Result.fail("参数错误");
        }
        if ("day".equals(type)){
            List<ProfitFlowLineChartVo> byDay = profitFlowMapper.getByDay(startTime);
            return Result.success(byDay);
        } else if ("month".equals(type)) {
            List<ProfitFlowLineChartVo> byMonth = profitFlowMapper.getByMonth(startTime);
            return Result.success(byMonth);
        }else {
            return Result.fail("参数错误");
        }
    }

    private Date getStartTime(String type) {
        if ("day".equals(type)){
            return DateUtils.getDateBefore(365);
        } else if ("month".equals(type)) {
            return DateUtils.getDateBefore(15);
        }else {
            return null;
        }
    }

}
