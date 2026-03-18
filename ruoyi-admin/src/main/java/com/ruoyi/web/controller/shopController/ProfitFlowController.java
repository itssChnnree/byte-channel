package com.ruoyi.web.controller.shopController;


import cn.hutool.core.util.ObjectUtil;
import com.ruoyi.system.domain.dto.ProfitFlowDto;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.service.IProfitFlowService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import javax.annotation.Resource;

/**
 * [利润流水表控制器]
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2026-03-15 01:04:05
 **/
@Api(tags = "利润流水表")
@RestController
@RequestMapping("profitFlow")
public class ProfitFlowController{

    @Resource(name = "profitFlowService")
    IProfitFlowService profitFlowService;

    /**
     * [增加利润流水]
     *
     * @param profitFlowDto 利润流水实体
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @ApiOperation("增加利润流水")
    @PostMapping("/add")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result add(@RequestBody ProfitFlowDto profitFlowDto) {
        // 参数校验
        if (ObjectUtil.isNull(profitFlowDto.getProfitAmount())) {
            LogEsUtil.warn("增加利润流水失败：利润金额不能为空");
            return Result.fail("利润金额不能为空");
        }
        return profitFlowService.addForController(profitFlowDto);
    }

    /**
     * [分页查询流水列表]
     *
     * @param profitFlowDto 查询条件
     * @return com.ruoyi.system.http.Result
     * @author chenxiangyue 2026/3/15
     **/
    @ApiOperation("分页查询流水列表")
    @PostMapping("/page")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result page(@RequestBody ProfitFlowDto profitFlowDto) {
        return profitFlowService.page(profitFlowDto);
    }

    @ApiOperation("获取利润总和")
    @PostMapping("/getSumProfit")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getSumProfit(@RequestBody ProfitFlowDto profitFlowDto) {
        return profitFlowService.sumProfit(profitFlowDto);
    }

    @ApiOperation("获取利润卡片")
    @GetMapping("/getProfitCard")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getProfitCard() {
        return profitFlowService.getProfitCard();
    }

    @ApiOperation("获取利润折线图")
    @GetMapping("/getProfitLine")
    @PreAuthorize("@ss.hasPermi('shop:background:admin')")
    public Result getProfitLine(String type) {
        return profitFlowService.getProfitLine(type);
    }

}
