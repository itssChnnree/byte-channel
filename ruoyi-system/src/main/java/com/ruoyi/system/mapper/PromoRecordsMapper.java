package com.ruoyi.system.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.dto.PromoRecordsDto;
import com.ruoyi.system.domain.entity.PromoRecords;
import com.ruoyi.system.domain.vo.PromoRecordsPageVo;
import com.ruoyi.system.domain.vo.PromoRecordsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.math.BigDecimal;
import java.util.List;

/**
 * 推广记录表(PromoRecords)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:20
 */
@Mapper
@Repository
public interface PromoRecordsMapper extends BaseMapper<PromoRecords> {


    /**
     * 通过订单id查询推广记录
     *
     * @param orderId 订单id
     * @return 推广记录
     */
    PromoRecords findByOrderId(@Param("orderId") String orderId);


    /**
     * 通过用户id查询已返现金额
     *
     * @param userId 用户id
     * @return 已返现金额
     */
    BigDecimal getReturnCashByUserId(@Param("userId") String userId, @Param("status") String status);


    /**
     * [获取已成功返现的人数]
     * @author 陈湘岳 2026/1/5
     * @param userId 查询用户的id
     * @param status 状态
     * @return java.lang.Integer
     **/
    Integer getReturnPeopleNum(@Param("userId") String userId,@Param("status") String status);


    /**
     * [分页查询邀请记录]
     * @author 陈湘岳 2026/1/5
     * @param promoRecordsDto
     * @return java.util.List<com.ruoyi.system.domain.vo.PromoRecordsVo>
     **/
    List<PromoRecordsPageVo> getRecords(@Param("dto") PromoRecordsDto promoRecordsDto,
                                        @Param("userId") String userId);

    /**
     * [查询待返现的推广记录]
     * 状态为WAIT_CONFIRM且订单完成超过24小时
     * @author 陈湘岳 2026/4/7
     * @return java.util.List<com.ruoyi.system.domain.entity.PromoRecords>
     **/
    List<PromoRecords> selectWaitCashbackRecords(@Param("limit") int limit);

    /**
     * [通过id上锁查询推广记录]
     * @author 陈湘岳 2026/4/7
     * @param id 推广记录id
     * @return com.ruoyi.system.domain.entity.PromoRecords
     **/
    PromoRecords selectByIdForUpdate(@Param("id") String id);
}
