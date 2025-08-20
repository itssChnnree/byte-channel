package com.ruoyi.system.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.vo.OrderVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单表(Order)�����ݿ���ʲ�
 *
 * @author chenxiangyue
 * @version v1.0.0
 * @date 2025-07-20 23:24:02
 */
@Mapper
@Repository
public interface OrderMapper extends BaseMapper<Order> {


    IPage<OrderVo> queryPage(IPage<Order> page, @Param("dto") OrderDto orderDto, @Param("userId") String userId);


    /**
     * [通过订单id查询订单]
     * @author 陈湘岳 2025/8/19
     * @param id 订单id
     * @return com.ruoyi.system.domain.entity.Order
     **/
    Order queryById(@Param("id") String id);
}
