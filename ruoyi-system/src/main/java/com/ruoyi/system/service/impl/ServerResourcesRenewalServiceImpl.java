package com.ruoyi.system.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.domain.dto.ServerResourcesRenewalDto;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import com.ruoyi.system.domain.entity.ServerResources;
import com.ruoyi.system.domain.entity.ServerResourcesRenewal;
import com.ruoyi.system.domain.vo.ServerResourcesRenewalVo;
import com.ruoyi.system.http.Result;
import com.ruoyi.system.mapper.PromoCodeRecordsMapper;
import com.ruoyi.system.mapper.PromoRecordsMapper;
import com.ruoyi.system.mapper.ServerResourcesMapper;
import com.ruoyi.system.mapper.ServerResourcesRenewalMapper;
import com.ruoyi.system.mapstruct.ServerResourcesRenewalMapstruct;
import com.ruoyi.system.service.IServerResourcesRenewalService;
import com.ruoyi.system.util.LogEsUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

/**
 * 服务器资源表(ServerResourcesRenewal)�����ʵ����
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-10-28 10:48:27
 */
@Service("serverResourcesRenewalService")
public class ServerResourcesRenewalServiceImpl  implements IServerResourcesRenewalService {

    @Resource
    private ServerResourcesRenewalMapper serverResourcesRenewalMapper;

    @Resource
    private ServerResourcesMapper serverResourcesMapper;

    @Resource
    private PromoCodeRecordsMapper promoCodeRecordsMapper;

    @Resource
    private ServerResourcesRenewalMapstruct serverResourcesRenewalMapstruct;

    /**
     * [续费设置变更]
     *
     * @param serverResourcesRenewalDto 变更入参
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/5
     **/
    @Override
    @Transactional
    public Result insertOrUpdate(ServerResourcesRenewalDto serverResourcesRenewalDto) {
        ServerResources serverResources = serverResourcesMapper.selectById(serverResourcesRenewalDto.getResourcesId());
        if (ObjectUtil.isEmpty(serverResources)){
            LogEsUtil.warn("用户配置自动续费的资源不存在，资源id："+serverResourcesRenewalDto.getResourcesId());
            return Result.fail("资源不存在");
        }
        if (!StrUtil.equals(serverResources.getResourceTenant(), SecurityUtils.getStrUserId())){
            LogEsUtil.warn("用户无权限配置自动续费资源，资源id："+serverResourcesRenewalDto.getResourcesId());
            return Result.fail("无权限配置当前资源");
        }
        serverResourcesRenewalMapper.deleteByResourcesId(serverResourcesRenewalDto.getResourcesId());
        LogEsUtil.info("已删除旧自动续费资源，资源id："+serverResourcesRenewalDto.getResourcesId());
        PromoCodeRecords promoCodeRecords = null;
        if (!StrUtil.isEmpty(serverResourcesRenewalDto.getRenewalPromo())){
            promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(serverResourcesRenewalDto.getRenewalPromo());
            if (ObjectUtil.isEmpty(promoCodeRecords)){
                LogEsUtil.warn("用户使用的优惠码不存在，优惠码："+ serverResourcesRenewalDto.getRenewalPromo());
                return Result.fail("优惠码不存在");
            }
        }
        ServerResourcesRenewal serverResourcesRenewal = buildServerResourcesRenewal(serverResourcesRenewalDto, serverResources, promoCodeRecords);
        serverResourcesRenewalMapper.insert(serverResourcesRenewal);
        LogEsUtil.info("自动续费插入类："+serverResourcesRenewal);
        return Result.success(serverResourcesRenewal);
    }

    /**
     * [根据资源id查询续费]
     *
     * @param resourcesId 资源id
     * @return com.ruoyi.system.http.Result
     * @author 陈湘岳 2026/3/5
     **/
    @Override
    public Result getByResourcesId(String resourcesId) {
        ServerResources serverResources = serverResourcesMapper.selectById(resourcesId);
        if (ObjectUtil.isEmpty(serverResources)){
            LogEsUtil.warn("用户配置自动续费资源不存在，资源id："+resourcesId);
            return Result.fail("资源不存在");
        }
        if (!StrUtil.equals(serverResources.getResourceTenant(), SecurityUtils.getStrUserId())){
            LogEsUtil.warn("用户无权限查询自动续费资源，资源id："+resourcesId);
            return Result.fail("无权限查询当前资源");
        }
        ServerResourcesRenewal byUserIdAndResourcesId = serverResourcesRenewalMapper.findByUserIdAndResourcesId(SecurityUtils.getStrUserId(), resourcesId);
        ServerResourcesRenewalVo serverResourcesRenewalVo = serverResourcesRenewalMapstruct.change2Vo(byUserIdAndResourcesId);
        if (ObjectUtil.isEmpty(serverResourcesRenewalVo)){
            ServerResourcesRenewalVo nullServerResourcesRenewalVo = new ServerResourcesRenewalVo();
            nullServerResourcesRenewalVo.setResourcesId(resourcesId);
            nullServerResourcesRenewalVo.setRenewalSwitch(0);
            return Result.success(nullServerResourcesRenewalVo);
        }
        if (!StrUtil.isEmpty(serverResourcesRenewalVo.getRenewalPromo())){
            PromoCodeRecords promoCodeRecords = promoCodeRecordsMapper.selectPromoCode(serverResourcesRenewalVo.getRenewalPromo());
            if (ObjectUtil.isNull(promoCodeRecords)){
                serverResourcesRenewalVo.setRenewalPromo(null);
                serverResourcesRenewalVo.setRenewalPromoIsExpired(true);
            }else {
                serverResourcesRenewalVo.setRenewalPromoIsExpired(false);
            }
        }
        return Result.success(serverResourcesRenewalVo);
    }

    private ServerResourcesRenewal buildServerResourcesRenewal(ServerResourcesRenewalDto serverResourcesRenewalDto,
                                                               ServerResources serverResources,
                                                               PromoCodeRecords promoCodeRecords){
        ServerResourcesRenewal serverResourcesRenewal = new ServerResourcesRenewal();
        serverResourcesRenewal.setId(serverResourcesRenewalDto.getId());
        serverResourcesRenewal.setResourcesId(serverResourcesRenewalDto.getResourcesId());
        serverResourcesRenewal.setRenewalSwitch(serverResourcesRenewalDto.getRenewalSwitch());
        serverResourcesRenewal.setRenewalPeriod(serverResourcesRenewalDto.getRenewalPeriod());
        serverResourcesRenewal.setUserId(SecurityUtils.getStrUserId());
        serverResourcesRenewal.setRenewalTime(getThreeDaysBefore(serverResources.getLeaseExpirationTime()));
        if (!ObjectUtil.isEmpty(promoCodeRecords)){
            serverResourcesRenewal.setRenewalPromo(promoCodeRecords.getPromoCode());
        }
        //是否支持优惠码失效后原价续费，默认0
        Optional.ofNullable(serverResourcesRenewalDto.getIsAgreeOriginalPrice()).
                ifPresentOrElse(serverResourcesRenewal::setIsAgreeOriginalPrice,
                        () -> serverResourcesRenewal.setIsAgreeOriginalPrice(0));
        //接受涨价的百分比,默认0
        Optional.ofNullable(serverResourcesRenewalDto.getAcceptablePriceIncreasePct()).
                ifPresentOrElse(serverResourcesRenewal::setAcceptablePriceIncreasePct,
                        () -> serverResourcesRenewal.setAcceptablePriceIncreasePct(0));
        if (!ObjectUtil.isEmpty(serverResourcesRenewalDto.getCurrentPriceSnapshot()) &&
                serverResourcesRenewalDto.getCurrentPriceSnapshot().compareTo(BigDecimal.ZERO) <= 0){
            LogEsUtil.warn("商品价格小于0："+ serverResourcesRenewalDto);
            throw new RuntimeException("商品价格小于0");
        }
        serverResourcesRenewal.setCurrentPriceSnapshot(serverResourcesRenewalDto.getCurrentPriceSnapshot());
        return serverResourcesRenewal;
    }

    //写一个方法，传入一个时间，获取这个时间3天之前的时间
    private Date getThreeDaysBefore(Date date){
        return new Date(date.getTime()-3*24*60*60*1000);
    }
}
