package org.rocket.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * 全局配置
 *
 * @author yangxin
 * 2020/07/30 20:51
 */
@Configuration
@ComponentScan(basePackages = {"org.rocket.order.*"})
//@MapperScan(basePackages = "org.rocket.order.mapping")
@MapperScan(basePackages = "org.rocket.order.mapper")
public class MainConfig {
}
