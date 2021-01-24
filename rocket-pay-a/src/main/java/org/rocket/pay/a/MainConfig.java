package org.rocket.pay.a;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangxin
 * 2021/01/25 00:16
 */
@Configuration
@MapperScan(basePackages = "org.rocket.pay.a.mapper")
@ComponentScan(basePackages = {"org.rocket.pay.a"})
public class MainConfig {

}
