package org.rocket.order.service.impl;

import org.rocket.order.constants.OrderStatusEnum;
import org.rocket.order.entity.Order;
import org.rocket.order.mapper.OrderMapper;
import org.rocket.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

/**
 * @author yangxin
 * 12/23/20 4:44 PM
 */
@Service
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public boolean createOrder(String cityId, String platformId, String userId, String supplierId, String goodIds) {
        boolean flag = false;
        Date currentTime = new Date();

        Order order = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType("1")
                .cityId(cityId)
                .platformId(platformId)
                .userId(userId)
                .supplierId(supplierId)
                .goodsId(goodIds)
                .orderStatus(OrderStatusEnum.ORDER_CREATED.getValue())
                .remark("")
                .createBy("admin")
                .createTime(currentTime)
                .updateBy("admin")
                .updateTime(currentTime)
                .build();
//        orderMapper.insertSelective(order);

        return false;
    }
}
