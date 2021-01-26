package org.rocket.pay.a.service.impl;

import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.SendStatus;
import org.apache.rocketmq.client.producer.TransactionSendResult;
import org.apache.rocketmq.common.message.Message;
import org.rocket.pay.a.entity.CustomerAccount;
import org.rocket.pay.a.mapper.CustomerAccountMapper;
import org.rocket.pay.a.service.PayService;
import org.rocket.pay.a.service.producer.TransactionProducer;
import org.rocket.pay.a.util.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;

/**
 * @author yangxin
 * 1/24/21 4:04 PM
 */
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Service
public class PayServiceImpl implements PayService {

    private final CustomerAccountMapper customerAccountMapper;

    private final TransactionProducer transactionProducer;

    public static final String TX_PAY_TOPIC = "tx_pay_topic";
    public static final String TX_PAY_TAG = "pay";

    @Autowired
    public PayServiceImpl(CustomerAccountMapper customerAccountMapper, TransactionProducer transactionProducer) {
        this.customerAccountMapper = customerAccountMapper;
        this.transactionProducer = transactionProducer;
    }

    @Override
    public String payment(String userId, String orderId, String accountId, double money) {
        String paymentReturned;

        /*
            最开始有一步token验证操作（防止自己重复提单问题）
         */
        BigDecimal payMoney = new BigDecimal(money);

        /*
            对大概率事件进行提前预判（小概率事件暂时放过，但最后保证数据一致性即可）

            业务出发：
                当前一个用户账号只允许一个线程（一个应用端访问）
            技术出发：
                1. redis去重（分布式锁）
                2. 数据库乐观锁去重
         */

        /*
            加锁开始（获取锁）
         */

        CustomerAccount old = customerAccountMapper.selectByPrimaryKey(accountId);
        BigDecimal currentBalance = old.getCurrentBalance();
        Integer currentVersion = old.getVersion();

        // 做扣款操作的时候，获得分布式锁，看一下能否获得
        BigDecimal newBalance = currentBalance.subtract(payMoney);

        /*
            加锁结束（释放锁）
         */

        if (newBalance.doubleValue() > 0) {
            /*
                 1. 组装消息
                 1. 执行本地事务
            */
            String key = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();

            Map<String, Object> paramMap = new HashMap<>(16);
            paramMap.put("userId", userId);
            paramMap.put("orderId", orderId);
            paramMap.put("accountId", accountId);
            paramMap.put("money", money);
            // 可能需要用到的参数
            paramMap.put("payMoney", payMoney);
            paramMap.put("newBalance", newBalance);
            paramMap.put("currentVersion", currentVersion);
            // 用于同步阻塞
            CountDownLatch countDownLatch = new CountDownLatch(1);
            paramMap.put("countDownLatch", countDownLatch);

            String json = FastJsonConvertUtil.convertObject2Json(paramMap);
            if (json == null) {
                return "支付失败！";
            }

            Message message = new Message(TX_PAY_TOPIC, TX_PAY_TAG, key, json.getBytes(StandardCharsets.UTF_8));
            // 消息发送并且本地的事务执行
            TransactionSendResult sendResult = transactionProducer.sendMessage(message, paramMap);

            try {
                // 同步阻塞
                countDownLatch.await();
                if (sendResult.getSendStatus() == SendStatus.SEND_OK
                        && sendResult.getLocalTransactionState() == LocalTransactionState.COMMIT_MESSAGE) {
                    paymentReturned = "支付成功。";
                } else {
                    paymentReturned = "支付失败！";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
                paymentReturned = "支付失败！";
            }
        } else {
            paymentReturned = "余额不足！";
        }

        return paymentReturned;
    }
}
