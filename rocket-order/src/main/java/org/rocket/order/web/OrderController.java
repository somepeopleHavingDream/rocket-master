package org.rocket.order.web;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.TimeUnit;

/**
 * @author yangxin
 * 2020/12/20 19:56
 */
@RestController
@Slf4j
public class OrderController {

    // 超时降级
    @HystrixCommand(commandKey = "createOrder",
            commandProperties = {
                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
            },
            fallbackMethod = "createOrderFallbackMethod4Timeout")
    @RequestMapping("/order/create")
    public String createOrder(@RequestParam("cityId") String cityId,
                              @RequestParam("platformId") String platformId,
                              @RequestParam("userId") String userId,
                              @RequestParam("supplierId") String supplierId,
                              @RequestParam("goodIds") String goodIds) throws InterruptedException {
        // 当前线程休眠5秒，测试断路器
        TimeUnit.SECONDS.sleep(5);

        return "下单成功";
    }

    public String createOrderFallbackMethod4Timeout(@RequestParam("cityId") String cityId,
                              @RequestParam("platformId") String platformId,
                              @RequestParam("userId") String userId,
                              @RequestParam("suppliedId") String suppliedId,
                              @RequestParam("goodIds") String goodIds) {
        log.info("超时降级策略执行！");
        return "hystrix timeout!";
    }
}
