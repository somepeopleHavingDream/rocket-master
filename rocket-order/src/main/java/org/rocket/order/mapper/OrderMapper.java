package org.rocket.order.mapper;

import org.apache.ibatis.annotations.Param;
import org.rocket.order.entity.Order;

import java.util.Date;

/**
 * @author yangxin
 * 2020/12/17 19:02
 */
public interface OrderMapper {

    int deleteByPrimaryKey(String orderId);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(String orderId);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    int updateOrderStatus(@Param("orderId") String orderId,
                          @Param("orderStatus") String orderStatus,
                          @Param("updateBy") String updateBy,
                          @Param("updateTime") Date updateTime);
}
