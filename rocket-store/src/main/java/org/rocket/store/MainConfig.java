package org.rocket.store;

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
@ComponentScan(basePackages = {"org.rocket.store.*"})
//@MapperScan(basePackages = "org.rocket.store.mapping")
@MapperScan(basePackages = "org.rocket.store.mapper")
public class MainConfig {
}
