package org.rocket.pay.b;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author yangxin
 * 2021/01/25 00:14
 */
@Configuration
@MapperScan(basePackages = "org.rocket.pay.b.mapper")
@ComponentScan(basePackages = {"org.rocket.pay.b"})
public class MainConfig {
}
