package org.rocket.pay.a.service.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

/**
 * @author yangxin
 * 2021/01/27 13:58
 */
@SuppressWarnings("SpellCheckingInspection")
@Component
public class SyncProducer {

    private final DefaultMQProducer producer;

    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.2:9876";
    private static final String PRODUCER_GROUP_NAME = "callback_pay_producer_group_name";

    private SyncProducer() {
        this.producer = new DefaultMQProducer(PRODUCER_GROUP_NAME);
        this.producer.setNamesrvAddr(NAMESRV_ADDR_SINGLE);
        this.producer.setRetryTimesWhenSendFailed(3);
        start();
    }

    public void start() {
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public SendResult sendMessage(Message message) {
        SendResult sendResult = null;
        try {
            sendResult = producer.send(message);
        } catch (MQClientException | InterruptedException | MQBrokerException | RemotingException e) {
            e.printStackTrace();
        }
        return sendResult;
    }

    public void shutdown() {
        producer.shutdown();
    }
}
