package org.rocket.order.web;

import lombok.extern.slf4j.Slf4j;
import org.rocket.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangxin
 * 2020/12/20 19:56
 */
@SuppressWarnings({"AlibabaCommentsMustBeJavadocFormat", "AlibabaRemoveCommentedCode"})
@RestController
@Slf4j
public class OrderController {

    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 超时降级
//    @HystrixCommand(
//            commandKey = "createOrder",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.timeout.enabled", value = "true"),
//                    @HystrixProperty(name = "execution.isolation.thread.timeoutInMilliseconds", value = "3000")
//            },
//            fallbackMethod = "createOrderFallbackMethod4Timeout"
//    )
    // 限流策略：线程池方式
//    @HystrixCommand(
//            commandKey = "createOrder",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.strategy", value = "THREAD")
//            },
//            threadPoolKey = "createOrderThreadPool",
//            threadPoolProperties = {
//                    @HystrixProperty(name = "coreSize", value = "10"),
//                    @HystrixProperty(name = "maxQueueSize", value = "20000"),
//                    @HystrixProperty(name = "queueSizeRejectionThreshold", value = "30"),
//            },
//            fallbackMethod = "createOrderFallbackMethod4Thread"
//    )
    // 限流策略：信号量方式
//    @HystrixCommand(
//            commandKey = "createOrder",
//            commandProperties = {
//                    @HystrixProperty(name = "execution.isolation.strategy", value = "SEMAPHORE"),
//                    @HystrixProperty(name = "execution.isolation.semaphore.maxConcurrentRequests", value = "3")
//            },
//            fallbackMethod = "createOrderFallbackMethod4Semaphore"
//    )
    @RequestMapping("/order/create")
    public String createOrder(@RequestParam("cityId") String cityId,
                              @RequestParam("platformId") String platformId,
                              @RequestParam("userId") String userId,
                              @RequestParam("supplierId") String supplierId,
                              @RequestParam("goodIds") String goodIds) {
        // 当前线程休眠5秒，测试断路器
//        TimeUnit.SECONDS.sleep(5);

        log.info("cityId: [{}], platformId: [{}], userId: [{}], supplierId: [{}], goodIds: [{}]",
                cityId,
                platformId,
                userId,
                supplierId,
                goodIds);

        return orderService.createOrder(cityId, platformId, userId, supplierId, goodIds) ? "下单成功！" : "下单失败！";
    }

    /**
     * 请求处理超时降级
     */
    public String createOrderFallbackMethod4Timeout(@RequestParam("cityId") String cityId,
                                                    @RequestParam("platformId") String platformId,
                                                    @RequestParam("userId") String userId,
                                                    @RequestParam("suppliedId") String suppliedId,
                                                    @RequestParam("goodIds") String goodIds) {
        log.info("超时降级策略执行！");
        return "hystrix timeout!";
    }

    /**
     * 限流策略：线程池方式
     */
    public String createOrderFallbackMethod4Thread(@RequestParam("cityId") String cityId,
                                                   @RequestParam("platformId") String platformId,
                                                   @RequestParam("userId") String userId,
                                                   @RequestParam("suppliedId") String suppliedId,
                                                   @RequestParam("goodIds") String goodIds) {
        log.info("线程池限流降级策略执行！");
        return "hystrix thread pool!";
    }

    /**
     * 限流策略：信号量方式
     */
    public String createOrderFallbackMethod4Semaphore(@RequestParam("cityId") String cityId,
                                                      @RequestParam("platformId") String platformId,
                                                      @RequestParam("userId") String userId,
                                                      @RequestParam("suppliedId") String suppliedId,
                                                      @RequestParam("goodIds") String goodIds) {
        log.info("信号量降级策略执行！");
        return "hystrix semaphore!";
    }
}
