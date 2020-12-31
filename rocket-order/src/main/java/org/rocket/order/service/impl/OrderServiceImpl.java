package org.rocket.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.rocket.order.constants.OrderStatusEnum;
import org.rocket.order.entity.Order;
import org.rocket.order.mapper.OrderMapper;
import org.rocket.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yangxin.store.service.api.StoreServiceAPI;

import java.util.Date;
import java.util.UUID;

/**
 * @author yangxin
 * 12/23/20 4:44 PM
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;

    @Reference(
            version = "1.0.0",
            application = "${dubbo.application.id}",
            interfaceName = "StoreServiceAPI",
            check = false,
            timeout = 1000
            // 读请求允许重试3次，写请求不进行重试（如果没做幂等处理）
    )
    private StoreServiceAPI storeServiceAPI;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    @Override
    public boolean createOrder(String cityId, String platformId, String userId, String supplierId, String goodIds) {
        // 标记创建订单是否成功
        boolean flag = true;

        // 创建订单
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

        // 当前版本，根据供应商Id和商品Id，获得当前商品记录的版本号，用于乐观锁操作
        int currentVersion = storeServiceAPI.selectVersion(supplierId, goodIds);
        // 乐观锁机制，通过获取到的商品版本号对商品记录进行更新
        /*
         * 此项目中updateStoreCountByVersion的sql逻辑中有这样的一个判断ts.version = #{version,jdbcType=INTEGER}，
         * 但其实在高并发的场景下，此处用户更新的订单的版本号可能会很快成为旧值，从而导致更新操作失败（即创建订单失败），这里其实是在这种机制下一个无法避免的性能瓶颈。
         * 更好的处理方法是用缓存来解决，数据库只是起到一个记录的功能。
         */
        int updateReturnCount = storeServiceAPI.updateStoreCountByVersion(currentVersion,
                supplierId,
                goodIds,
                "admin",
                currentTime);

        if (updateReturnCount == 1) {
            // ！！！！如果出现SQL异常入库失败，那么要对库存的数量和版本号进行回滚操作
            // 所以，对于此处的持久化操作最好用一个try-catch语句包裹
            orderMapper.insertSelective(order);
        } else if (updateReturnCount == 0) {
            // 标识下单失败
            flag = false;

            // 没有更新成功，1：高并发时乐观锁生效；2：库存不足
            int currentStoreCount = storeServiceAPI.selectStoreCount(supplierId, goodIds);
            if (currentStoreCount == 0) {
                // {flag:false, messageCode:xxx, message: "当前库存不足"}
                log.info("当前库存不足……");
            } else {
                // {flag:false, messageCode:xxx, message: "乐观锁生效"}
                log.info("乐观锁生效……");
            }
        }

        return flag;
    }
}
