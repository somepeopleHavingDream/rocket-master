package org.rocket.pay.a.service.producer;

import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.rocket.pay.a.util.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author yangxin
 * 2021/01/27 13:54
 */
@SuppressWarnings({"unused", "SpellCheckingInspection"})
@Service
public class CallbackService {

    public static final String CALLBACK_PAY_TOPIC = "callback_pay_topic";
    public static final String CALLBACK_PAY_TAGS = "callback_pay";
    private static final String NAMESRV_ADDR_SINGLE = "192.168.3.2:9876";

    private final SyncProducer syncProducer;

    @Autowired
    public CallbackService(SyncProducer syncProducer) {
        this.syncProducer = syncProducer;
    }

    public void sendOkMessage(String orderId, String userId) {
        Map<String, Object> paramMap = new HashMap<>(4);
        paramMap.put("userId", userId);
        paramMap.put("orderId", orderId);
        // 2代表ok
        paramMap.put("status", "2");

        String json = FastJsonConvertUtil.convertObject2Json(paramMap);
        if (json == null) {
            return;
        }

        String key = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();
        Message message = new Message(CALLBACK_PAY_TOPIC, CALLBACK_PAY_TAGS, key, json.getBytes(StandardCharsets.UTF_8));

        SendResult sendResult = syncProducer.sendMessage(message);
    }
}
