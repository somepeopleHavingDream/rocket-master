package org.rocket.pay.a.service;

/**
 * @author yangxin
 * 1/24/21 3:54 PM
 */
public interface PayService {

    /**
     * 用户支付
     * @param userId 用户Id
     * @param orderId 订单Id
     * @param accountId 账号Id
     * @param money 金额
     * @return 返回的金额
     */
    String payment(String userId, String orderId, String accountId, double money);
}
