package org.rocket.order.web;

import com.alibaba.dubbo.config.annotation.Reference;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.yangxin.store.service.api.HelloServiceAPI;

/**
 * @author yangxin
 * 2020/12/20 16:10
 */
@RestController
@Slf4j
public class HelloController {

    @Reference(
            version = "1.0.0",
            application = "${dubbo.application.id}",
            interfaceName = "HelloServiceAPI",
            check = false,
            timeout = 3000
            // 读请求允许重试3次，写请求不进行重试（如果没做幂等处理）
    )
    private HelloServiceAPI helloServiceAPI;

    @RequestMapping("/hello")
    public String hello(@RequestParam("name") String name) {
        log.info("name: [{}]", name);
        return helloServiceAPI.sayHello(name);
    }
}
