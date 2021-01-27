package org.rocket.pay.a.service.producer;

import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.LocalTransactionState;
import org.apache.rocketmq.client.producer.TransactionListener;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.common.message.MessageExt;
import org.rocket.pay.a.mapper.CustomerAccountMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @author yangxin
 * 1/24/21 5:08 PM
 */
@SuppressWarnings({"SpringJavaInjectionPointsAutowiringInspection", "unchecked", "unused"})
@Component
@Slf4j
public class TransactionListenerImpl implements TransactionListener {

    private final CustomerAccountMapper customerAccountMapper;

    @Autowired
    public TransactionListenerImpl(CustomerAccountMapper customerAccountMapper) {
        log.info("TransactionListenerImpl initialize.");
        this.customerAccountMapper = customerAccountMapper;
    }

    @Override
    public LocalTransactionState executeLocalTransaction(Message message, Object o) {
        log.info("执行本地事务单元。");

        Map<String, Object> paramMap = (Map<String, Object>) o;
        String userId = (String) paramMap.get("userId");
        String accountId = (String) paramMap.get("accountId");
        String orderId = (String) paramMap.get("orderId");
        // 当前的支付款
        BigDecimal payMoney = (BigDecimal) paramMap.get("payMoney");
        // 前置扣款成功的余额
        BigDecimal newBalance = (BigDecimal) paramMap.get("newBalance");
        int currentVersion = (int) paramMap.get("currentVersion");
        CountDownLatch countDownLatch = (CountDownLatch) paramMap.get("countDownLatch");

        // updateBalance传递当前的支付款，数据库操作
//        Date currentTime = new Date();

        try {
            int count = customerAccountMapper.updateBalance(accountId, newBalance, currentVersion);
            return count == 1 ? LocalTransactionState.COMMIT_MESSAGE : LocalTransactionState.UNKNOW;
        } catch (Exception e) {
            e.printStackTrace();
            return LocalTransactionState.ROLLBACK_MESSAGE;
        } finally {
            countDownLatch.countDown();
        }
    }

    @Override
    public LocalTransactionState checkLocalTransaction(MessageExt messageExt) {
        return null;
    }
}
