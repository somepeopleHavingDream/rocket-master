package org.rocket.store.service.provider;

import com.alibaba.dubbo.config.annotation.Service;
import lombok.extern.slf4j.Slf4j;
import org.yangxin.store.service.api.HelloServiceApi;

/**
 * @author yangxin
 * 2020/12/20 15:56
 */
@Service(version = "1.0.0",
        application = "${dubbo.application.id}",
        protocol = "${dubbo.protocol.id}",
        registry = "${dubbo.registry.id}")
@Slf4j
public class HelloServiceProvider implements HelloServiceApi {

    @Override
    public String sayHello(String name) {
        log.info("name: [{}]", name);
        return "hello " + name;
    }
}
