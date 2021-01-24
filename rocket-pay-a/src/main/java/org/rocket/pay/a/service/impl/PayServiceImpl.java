package org.rocket.pay.a.service.impl;

import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;
import org.rocket.pay.a.entity.CustomerAccount;
import org.rocket.pay.a.mapper.CustomerAccountMapper;
import org.rocket.pay.a.service.PayService;
import org.rocket.pay.a.util.FastJsonConvertUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author yangxin
 * 1/24/21 4:04 PM
 */
@Service
public class PayServiceImpl implements PayService {

    private final CustomerAccountMapper customerAccountMapper;

    public static final String TX_PAY_TOPIC = "tx_pay_topic";
    public static final String TX_PAY_TAG = "pay";

    @Autowired
    public PayServiceImpl(CustomerAccountMapper customerAccountMapper) {
        this.customerAccountMapper = customerAccountMapper;
    }

    @Override
    public String payment(String userId, String orderId, String accountId, double money) {
        String paymentReturned = "";

        BigDecimal payMoney = new BigDecimal(money);

        CustomerAccount old = customerAccountMapper.selectByPrimaryKey(accountId);
        BigDecimal currentBalance = old.getCurrentBalance();
        Integer currentVersion = old.getVersion();
        BigDecimal newBalance = currentBalance.subtract(payMoney);
        if (newBalance.doubleValue() > 0) {
            // 1. 组装消息
            String key = UUID.randomUUID().toString() + "$" + System.currentTimeMillis();

            Map<String, Object> paramMap = new HashMap<>(4);
            paramMap.put("userId", userId);
            paramMap.put("orderId", orderId);
            paramMap.put("accountId", accountId);
            paramMap.put("money", money);

            String json = FastJsonConvertUtil.convertObject2Json(paramMap);
            if (json != null) {
                Message message = new Message(TX_PAY_TOPIC, TX_PAY_TAG, key, json.getBytes(StandardCharsets.UTF_8));
            }

            // 1. 执行本地事务
        }
        return paymentReturned;
    }
}
