package org.rocket.pkg.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageConst;
import org.apache.rocketmq.common.message.MessageExt;
import org.rocket.pkg.util.FastJsonConvertUtil;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author yangxin
 * 1/27/21 5:05 PM
 */
@SuppressWarnings("SpellCheckingInspection")
@Component
@Slf4j
public class PkgOrderlyConsumer {

    private DefaultMQPushConsumer consumer;

    public static final String PKG_TOPIC = "pkg_topic";
    public static final String PKG_TAGS = "pkg";
    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.2:9876";
    public static final String CONSUMER_GROUP_NAME = "orderly_consumer_group_name";

    private PkgOrderlyConsumer() throws MQClientException {
        this.consumer = new DefaultMQPushConsumer(CONSUMER_GROUP_NAME);
        this.consumer.setConsumeThreadMin(10);
        this.consumer.setConsumeThreadMax(30);
        this.consumer.setNamesrvAddr(NAMESRV_ADDR_SINGLE);
        this.consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        this.consumer.subscribe(PKG_TOPIC, PKG_TAGS);
        this.consumer.setMessageListener(new PkgOrderlyListener());
        this.consumer.start();
    }

    @SuppressWarnings("unchecked")
    private class PkgOrderlyListener implements MessageListenerOrderly {

        private final Random random = new Random();

        @Override
        public ConsumeOrderlyStatus consumeMessage(List<MessageExt> messageExtList, ConsumeOrderlyContext context) {
            for (MessageExt messageExt : messageExtList) {
                try {
                    String topic = messageExt.getTopic();
                    String msgBody = new String(messageExt.getBody(), StandardCharsets.UTF_8);
                    String tags = messageExt.getTags();
                    String keys = messageExt.getKeys();
                    String orignMsgId = messageExt.getProperties().get(MessageConst.PROPERTY_ORIGIN_MESSAGE_ID);
                    log.info("topic: [{}], tags: [{}], keys: [{}], msg: [{}], orignMsgId: [{}]",
                            topic, tags, keys, msgBody, orignMsgId);

                    Map<String, Object> map = (Map<String, Object>) FastJsonConvertUtil.convertJson2Object(msgBody, Map.class);
                    if (map == null) {
                        return null;
                    }

                    String orderId = (String) map.get("orderId");
                    String userId = (String) map.get("userId");
                    String text = (String) map.get("text");

                    // 模拟实际的业务耗时操作
                    // ps：创建包裹信息、对物流的服务调用（异步调用）
                    TimeUnit.SECONDS.sleep(random.nextInt(3) + 1);

                    log.info("text: [{}]", text);
                } catch (Exception e) {
                    e.printStackTrace();

                    int reconsumeTimes = messageExt.getReconsumeTimes();
                    int maxReconsumeTimes = 3;
                    if (reconsumeTimes >= maxReconsumeTimes) {
                        // 记录日志
                        // 做补偿处理
                        log.error("重新消费次数达至3次，不再处理！");
                        return ConsumeOrderlyStatus.SUCCESS;
                    }
                    return ConsumeOrderlyStatus.SUSPEND_CURRENT_QUEUE_A_MOMENT;
                }
            }
            return ConsumeOrderlyStatus.SUCCESS;
        }
    }
}
