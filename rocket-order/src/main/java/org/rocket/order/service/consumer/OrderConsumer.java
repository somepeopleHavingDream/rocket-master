package org.rocket.order.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.rocket.order.constants.OrderStatusEnum;
import org.rocket.order.mapper.OrderMapper;
import org.rocket.order.service.OrderService;
import org.rocket.order.util.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author yangxin
 * 1/27/21 2:16 PM
 */
@SuppressWarnings({"SpellCheckingInspection", "SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
@Component
@Slf4j
public class OrderConsumer {

    public static final String CALLBACK_PAY_TOPIC = "callback_pay_topic";
    public static final String CALLBACK_PAY_TAGS = "callback_pay";
    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.2:9876";

    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderService orderService;

    public OrderConsumer() throws MQClientException {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer("callback_pay_consumer_group");
        consumer.setConsumeThreadMin(10);
        consumer.setConsumeThreadMax(50);
        consumer.setNamesrvAddr(NAMESRV_ADDR_SINGLE);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        consumer.subscribe(CALLBACK_PAY_TOPIC, CALLBACK_PAY_TAGS);
        consumer.registerMessageListener(new MessageListenerConcurrently4Pay());
        consumer.start();
    }

    @SuppressWarnings("unchecked")
    private class MessageListenerConcurrently4Pay implements MessageListenerConcurrently {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList,
                                                        ConsumeConcurrentlyContext context) {
            MessageExt messageExt = messageExtList.get(0);

            try {
                String topic = messageExt.getTopic();
                String msgBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                String tags = messageExt.getTags();
                String keys = messageExt.getKeys();
                String orignMsgId = messageExt.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                log.info("topic: [{}], tags: [{}], keys: [{}], msg: [{}], orignMsgId: [{}]",
                        topic, tags, keys, msgBody, orignMsgId);

                // 通过keys进行去重表去重，或者使用redis进行去重？不需要。
                Map<String, Object> map = (Map<String, Object>) FastJsonConvertUtil.convertJson2Object(msgBody, Map.class);
                if (map == null) {
                    return null;
                }

                String orderId = (String) map.get("orderId");
                String userId = (String) map.get("userId");
                String status = (String) map.get("status");

                Date currentTime = new Date();
                if (Objects.equals(status, OrderStatusEnum.ORDER_PAYED.getValue())) {
                    int count = orderMapper.updateOrderStatus(orderId, status, "admin", currentTime);
                    if (count == 1) {
                        orderService.sendOrderlyMessage4Pkg(userId, orderId);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();

                int reconsumeTimes = messageExt.getReconsumeTimes();
                int maxReconsumeTimes = 3;
                if (reconsumeTimes >= maxReconsumeTimes) {
                    // 记录日志
                    // 做补偿处理
                    log.error("重新消费次数达至3次，不再处理！");
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }

            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        }
    }
}
