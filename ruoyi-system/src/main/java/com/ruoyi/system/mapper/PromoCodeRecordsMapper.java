package com.ruoyi.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.PromoCodeRecords;
import com.ruoyi.system.domain.vo.PromoCodeRecordsVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;


/**
 * 推广码记录表(PromoCodeRecords)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:13
 */
@Mapper
@Repository
public interface PromoCodeRecordsMapper extends BaseMapper<PromoCodeRecords> {


    /**
     * [通过邀请码查询]
     * @author 陈湘岳 2025/8/13
     * @param code 邀请码
     * @return com.ruoyi.system.domain.entity.PromoCodeRecords
     **/
    PromoCodeRecords selectPromoCode(@Param("code")String code);


    /**
     * [查询指定状态的邀请码]
     * @author 陈湘岳 2025/12/29
     * @param status 状态
     * @return com.ruoyi.system.domain.entity.PromoCodeRecords
     **/
    PromoCodeRecordsVo findPromoCodeRecords(@Param("status") String status);


    /**
     * [通过订单ID查询该订单所用邀请码]
     * @author 陈湘岳 2025/8/14
     * @param orderId
     * @return com.ruoyi.system.domain.entity.PromoCodeRecords
     **/
    PromoCodeRecords findByOrderId(@Param("orderId")String orderId);

    /**
     * [更新状态]
     * @author 陈湘岳 2025/8/14
     * @param userId 用户id
     * @param status 状态
     * @return int
     **/
    int updateStatus(@Param("userId")String userId, @Param("status")String status);




}
