package org.rocket.pay.a.service.producer;

import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.TransactionMQProducer;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.*;

/**
 * @author yangxin
 * 1/24/21 5:11 PM
 */
@SuppressWarnings({"unused", "SpellCheckingInspection", "SpringJavaAutowiredFieldsWarningInspection"})
@Component
public class TransactionProducer implements InitializingBean {

    private final TransactionMQProducer producer;

    @Autowired
    private TransactionListenerImpl transactionListener;

    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.2:9876";
//    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.3:9876";

    private static final String PRODUCER_GROUP_NAME = "tx_pay_producer_group_name";

    private TransactionProducer() {
        this.producer = new TransactionMQProducer(PRODUCER_GROUP_NAME);
        ExecutorService executorService = new ThreadPoolExecutor(2,
                5,
                100,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(2000),
                r -> {
                    Thread thread = new Thread(r);
                    thread.setName(PRODUCER_GROUP_NAME + "-check-thread");

                    return thread;
                });
        this.producer.setExecutorService(executorService);
        this.producer.setNamesrvAddr(NAMESRV_ADDR_SINGLE);
    }

    @Override
    public void afterPropertiesSet() {
        producer.setTransactionListener(transactionListener);
        start();
    }

    private void start() {
        try {
            producer.start();
        } catch (MQClientException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        producer.shutdown();
    }

    public TransactionSendResult sendMessage(Message message, Object argument) {
        TransactionSendResult sendResult = null;
        try {
            sendResult = producer.sendMessageInTransaction(message, argument);
        } catch (MQClientException e) {
            e.printStackTrace();
        }

        return sendResult;
    }
}
