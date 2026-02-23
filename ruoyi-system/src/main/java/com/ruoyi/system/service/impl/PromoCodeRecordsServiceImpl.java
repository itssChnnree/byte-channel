package com.ruoyi.system.service.impl;



import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.constant.EntityStatus;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import com.ruoyi.system.domain.vo.PromoCodeRecordsVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.PromoCodeRecordsMapper;
import com.ruoyi.system.service.IPromoCodeRecordsService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 推广码记录表(PromoCodeRecords)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:14
 */
@Service("promoCodeRecordsService")
public class PromoCodeRecordsServiceImpl implements IPromoCodeRecordsService {

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;


    /**
     * [生成推广码]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/8/14
     **/
    @Override
    @Transactional
    public Result createPromoCodeRecords() {
        PromoCodeRecordsVo oldPromoCodeRecords = promoCodeRecordsMapper.findPromoCodeRecords(EntityStatus.NORMAL,SecurityUtils.getStrUserId());
        boolean over30Days = DateUtils.isOver30Days(oldPromoCodeRecords.getCreateTime());
        if (!over30Days){
            LogEsUtil.warn("30天内已成功生成过邀请码,用户id："+SecurityUtils.getStrUserId());
            return Result.fail("您30天内已成功生成过邀请码，请过段时间再试");
        }
        //将历史推广码失效
        promoCodeRecordsMapper.updateStatus(SecurityUtils.getStrUserId(), EntityStatus.DISABLED);
        //生成新推广码
        String code = ServerResourcesServiceImpl.generateRandomString(10);
        PromoCodeRecords promoCodeRecords = new PromoCodeRecords();
        promoCodeRecords.setPromoCode(code);
        promoCodeRecords.setUserId(SecurityUtils.getStrUserId());
        promoCodeRecords.setStatus(EntityStatus.NORMAL);
        int insert = promoCodeRecordsMapper.insert(promoCodeRecords);
        return insert > 0 ? Result.success(promoCodeRecords) : Result.fail();
    }


    /**
     * [获取推广码]
     *
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2025/12/29
     **/
    @Override
    public Result getPromoCodeRecords() {
        PromoCodeRecordsVo promoCodeRecords = promoCodeRecordsMapper.findPromoCodeRecords(EntityStatus.NORMAL,SecurityUtils.getStrUserId());
        return Result.success(promoCodeRecords);
    }
}
