package org.rocket.order.service.producer;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author yangxin
 * 1/27/21 2:58 PM
 */
@SuppressWarnings({"SpellCheckingInspection", "unused"})
@Component
public class OrderlyProducer {

    private final DefaultMQProducer producer;

    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.2:9876";
    public static final String PRODUCER_GROUP_NAME = "orderly_producer_group_name";

    private OrderlyProducer() {
        this.producer = new DefaultMQProducer(PRODUCER_GROUP_NAME);
        this.producer.setNamesrvAddr(NAMESRV_ADDR_SINGLE);
        this.producer.setSendMsgTimeout(3000);
        start();
    }

    public void start() {
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        producer.shutdown();
    }

    public void sendOrderlyMessages(List<Message> messageList, int messageQueueNumber) {
        for (Message message : messageList) {
            try {
                producer.send(message, (mqs, msg, arg) -> {
                    int id = (int) arg;
                    return mqs.get(id);
                }, messageQueueNumber);
            } catch (MQClientException | RemotingException | MQBrokerException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
