package com.ruoyi.system.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ruoyi.system.domain.entity.PromoRecords;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

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

}
