package org.rocket.pay.b.service.consumer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.*;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.rocket.pay.b.entity.PlatformAccount;
import org.rocket.pay.b.mapper.PlatformAccountMapper;
import org.rocket.pay.b.util.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author yangxin
 * 1/24/21 9:40 PM
 */
@SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpellCheckingInspection", "SpringJavaInjectionPointsAutowiringInspection"})
@Component
@Slf4j
public class PayConsumer {

    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.2:9876";
//    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.3:9876";

    private static final String CONSUMER_GROUP_NAME = "tx_pay_consumer_group_name";

    public static final String TX_PAY_TOPIC = "tx_pay_topic";
    public static final String TX_PAY_TAG = "pay";

    @Autowired
    private PlatformAccountMapper platformAccountMapper;

    private PayConsumer() {
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(CONSUMER_GROUP_NAME);
        consumer.setConsumeThreadMin(10);
        consumer.setConsumeThreadMin(30);
        consumer.setNamesrvAddr(NAMESRV_ADDR_SINGLE);
        consumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_LAST_OFFSET);
        try {
            consumer.subscribe(TX_PAY_TOPIC, TX_PAY_TAG);
            consumer.registerMessageListener(new MessageListenerConcurrently4Pay());
            consumer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings({"unchecked", "unused"})
    private class MessageListenerConcurrently4Pay implements MessageListenerConcurrently {

        @Override
        public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messageExtList,
                                                        ConsumeConcurrentlyContext context) {
            MessageExt messageExt = messageExtList.get(0);
            try {
                String topic = messageExt.getTopic();
                String tags = messageExt.getTags();
                String keys = messageExt.getKeys();
                String body = new String(messageExt.getBody(), RemotingHelper.DEFAULT_CHARSET);
                log.info("收到事务消息，topic: [{}], tags: [{}], keys: [{}], body: [{}]", topic, tags, keys, body);

                /*
                    消息一旦过来（去重幂等操作）
                    数据库主键去重或redis去重
                 */
                Map<String, Object> paramMap = (Map<String, Object>) FastJsonConvertUtil
                        .convertJson2Object(body, Map.class);
                if (paramMap == null) {
                    return ConsumeConcurrentlyStatus.RECONSUME_LATER;
                }
                String userId = (String) paramMap.get("userId");
                String accountId = (String) paramMap.get("accountId");
                String orderId = (String) paramMap.get("orderId");
                // 当前的支付款
                BigDecimal money = (BigDecimal) paramMap.get("money");

                PlatformAccount platformAccount = platformAccountMapper.selectByPrimaryKey("platform001");
                platformAccount.setCurrentBalance(platformAccount.getCurrentBalance().add(money));
                Date currentTime = new Date();
                platformAccount.setVersion(platformAccount.getVersion() + 1);
                platformAccount.setDateTime(currentTime);
                platformAccount.setUpdateTime(currentTime);
                platformAccountMapper.updateByPrimaryKeySelective(platformAccount);
            } catch (UnsupportedEncodingException e) {
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
