package com.ruoyi.system.service.impl;


import cn.hutool.core.util.DesensitizedUtil;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.utils.PageUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.OrderStatus;
import com.ruoyi.system.domain.dto.PromoRecordsDto;
import com.ruoyi.system.domain.vo.PromoRecordsPageVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.PromoCodeRecordsMapper;
import com.ruoyi.system.mapper.PromoRecordsMapper;
import com.ruoyi.system.service.IPromoRecordsService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

/**
 * 推广记录表(PromoRecords)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:20
 */
@Service("promoRecordsService")
public class PromoRecordsServiceImpl  implements IPromoRecordsService {


    @Resource
    private PromoRecordsMapper promoRecordsMapper;



    /**
     * [获取已返现金额]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/1/5
     **/
    @Override
    public Result getReturnCash() {
        BigDecimal returnCashByUserId = promoRecordsMapper.getReturnCashByUserId(SecurityUtils.getStrUserId(), OrderStatus.RETURN_CASH);
        //将返现金额转为字符串，保留两位小数
        if (returnCashByUserId == null) {
            return Result.success("成功","0");        }
        String returnCash = returnCashByUserId.setScale(2, BigDecimal.ROUND_HALF_UP).toString();
        return Result.success("成功",returnCash);
    }


    /**
     * [获取已成功邀请人数]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/1/5
     **/
    @Override
    public Result getReturnPeopleNum() {
        Integer returnPeopleNum = promoRecordsMapper.getReturnPeopleNum(SecurityUtils.getStrUserId(), OrderStatus.RETURN_CASH);
        return Result.success(returnPeopleNum);
    }


    /**
     * [查询推广记录]
     *
     * @param promoRecordsDto 查询入参
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/1/5
     **/
    @Override
    public Result getRecords(PromoRecordsDto promoRecordsDto) {
        PageUtils.startPage(promoRecordsDto);
        List<PromoRecordsPageVo> records = promoRecordsMapper.getRecords(promoRecordsDto, SecurityUtils.getStrUserId());
        records.parallelStream().forEach(promoRecordsPageVo -> {
            String maskedEmail = DesensitizedUtil.desensitized(promoRecordsPageVo.getReferralsUserName(), DesensitizedUtil.DesensitizedType.EMAIL);
            promoRecordsPageVo.setReferralsUserName(maskedEmail);
        });
        return Result.success(new PageInfo<>(records));
    }
}
