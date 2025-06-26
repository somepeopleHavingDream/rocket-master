package org.rocket.order.service.impl;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.common.message.Message;
import org.rocket.order.constants.OrderStatusEnum;
import org.rocket.order.entity.Order;
import org.rocket.order.mapper.OrderMapper;
import org.rocket.order.service.OrderService;
import org.rocket.order.service.producer.OrderlyProducer;
import org.rocket.order.util.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.yangxin.store.service.api.StoreServiceApi;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author yangxin
 * 12/23/20 4:44 PM
 */
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    public static final String PKG_TOPIC = "pkg_topic";
    public static final String PKG_TAGS = "pkg";

    private final OrderMapper orderMapper;
    private final OrderlyProducer orderlyProducer;

    @Reference(
            version = "1.0.0",
            application = "${dubbo.application.id}",
            interfaceName = "StoreServiceAPI",
            check = false,
            timeout = 1000
            // 读请求允许重试3次，写请求不进行重试（如果没做幂等处理）
    )
    private StoreServiceApi storeServiceApi;

    @Autowired
    public OrderServiceImpl(OrderMapper orderMapper, OrderlyProducer orderlyProducer) {
        this.orderMapper = orderMapper;
        this.orderlyProducer = orderlyProducer;
    }

    @Override
    public boolean createOrder(String cityId, String platformId, String userId, String supplierId, String goodIds) {
        boolean flag = true;
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

        // 当前版本
        // 当前版本，根据供应商Id和商品Id，获得当前商品记录的版本号，用于乐观锁操作
        int currentVersion = storeServiceApi.selectVersion(supplierId, goodIds);

        /*
         * 此项目中updateStoreCountByVersion的sql逻辑中有这样的一个判断ts.version = #{version,jdbcType=INTEGER}，
         * 但其实在高并发的场景下，此处用户更新的订单的版本号可能会很快成为旧值，从而导致更新操作失败（即创建订单失败），
         * 这里其实是在这种机制下一个无法避免的性能瓶颈。
         * 更好的处理方法是用缓存来解决，数据库只是起到一个记录的作用。
         */
        int updateReturnCount = storeServiceApi.updateStoreCountByVersion(currentVersion,
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
            int currentStoreCount = storeServiceApi.selectStoreCount(supplierId, goodIds);
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

    @Override
    public void sendOrderlyMessage4Pkg(String userId, String orderId) {
        List<Message> messageList = new ArrayList<>();

        Message message1 = buildOrderlyMessage4Pkg(userId, orderId, "创建包裹操作1");
        messageList.add(message1);

        Message message2 = buildOrderlyMessage4Pkg(userId, orderId, "发送物流通知操作2");
        messageList.add(message2);

        // 顺序消息投递是应该按照供应商Id与topic和messagequeueId进行绑定对应的
        // supplier_id
        Order order = orderMapper.selectByPrimaryKey(orderId);
        int messageQueueNumber = Integer.parseInt(order.getSupplierId());

        // 对应的顺序消息的生产者把messageList发出去
        orderlyProducer.sendOrderlyMessages(messageList, messageQueueNumber);
    }

    private Message buildOrderlyMessage4Pkg(String userId, String orderId, String text) {
        Map<String, Object> paramMap = buildOrderLyMessageMap(userId, orderId, text);
        String json = FastJsonConvertUtil.convertObject2Json(paramMap);
        if (json == null) {
            return null;
        }

        String key = UUID.randomUUID() + "$" + System.currentTimeMillis();
        return new Message(PKG_TOPIC, PKG_TAGS, key, json.getBytes(StandardCharsets.UTF_8));
    }

    private Map<String, Object> buildOrderLyMessageMap(String userId, String orderId, String text) {
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("userId", userId);
        paramMap.put("orderId", orderId);
        paramMap.put("text", text);

        return paramMap;
    }
}
