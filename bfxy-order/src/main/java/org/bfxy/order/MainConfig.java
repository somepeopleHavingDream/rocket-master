package org.bfxy.order;

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
@ComponentScan(basePackages = {"org.bfxy.order.*"})
@MapperScan(basePackages = "org.bfxy.order.mapper")
public class MainConfig {
}
