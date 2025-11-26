package com.ruoyi.system.mapper;


import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import com.ruoyi.system.domain.entity.OrderInformationSnapshot;

/**
 * 订单快照(OrderInformationSnapshot
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-11-22 15:11:23
 */
@Mapper
@Repository
public interface OrderInformationSnapshotMapper extends BaseMapper<OrderInformationSnapshot> {

}
