package com.ruoyi.system.mapper;



import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.ruoyi.system.domain.dto.OrderDto;
import com.ruoyi.system.domain.entity.Order;
import com.ruoyi.system.domain.vo.OrderInfoVo;
import com.ruoyi.system.domain.vo.OrderVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

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


    /**
     * [分页查询订单状态]
     * @author 陈湘岳 2025/11/3
     * @param orderDto 订单参数
     * @param userId 用户id
     * @return java.util.List<com.ruoyi.system.domain.vo.OrderVo>
     **/
    List<OrderVo> queryPage(@Param("dto") OrderDto orderDto, @Param("userId") String userId);


    /**
     * [通过订单id查询订单]
     * @author 陈湘岳 2025/8/19
     * @param id 订单id
     * @return com.ruoyi.system.domain.entity.Order
     **/
    Order queryById(@Param("id") String id);

    /**
     * [根据订单id修改订单状态]
     * @author 陈湘岳 2025/10/16
     * @param id 订单id
     * @param status 订单状态
     * @return int
     **/
    int updateStatusById(@Param("id") String id, @Param("status") String status);
    
    
    /**
     * 查询订单信息
     * @author 陈湘岳 2025/9/3
     * @param id 订单id
     * @param userId 用户id
     * @return com.ruoyi.system.domain.vo.OrderInfoVo
     **/
    OrderInfoVo getOrderInfoById(@Param("id") String id, @Param("userId") String userId);


    /**
     * 查询续费订单信息
     * @author 陈湘岳 2025/9/3
     * @param id 订单id
     * @param userId 用户id
     * @return com.ruoyi.system.domain.vo.OrderInfoVo
     **/
    OrderInfoVo getRenewalOrderInfoById(@Param("id") String id, @Param("userId") String userId);
}
