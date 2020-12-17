package org.rocket.order.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author yangxin
 * 2020/08/25 20:45
 */
@RestController
@Slf4j
public class IndexController {

    @RequestMapping("/index")
    public String index() {
//        System.out.println("------------");
        log.info("----------------");
        return "index";
    }
}
