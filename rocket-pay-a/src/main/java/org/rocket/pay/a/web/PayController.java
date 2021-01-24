package org.rocket.pay.a.web;

import org.rocket.pay.a.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangxin
 * 1/24/21 3:50 PM
 */
@RestController
public class PayController {

    private final PayService payService;

    @Autowired
    public PayController(PayService payService) {
        this.payService = payService;
    }

    @RequestMapping("/pay")
    public String pay(@RequestParam("userId") String userId,
                      @RequestParam("orderId") String orderId,
                      @RequestParam("accountId") String accountId,
                      @RequestParam("money") Double money) {
        return payService.payment(userId, orderId, accountId, money);
    }
}
