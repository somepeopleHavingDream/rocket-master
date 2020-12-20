package org.rocket.order.config.hystrix;

import com.netflix.hystrix.contrib.javanica.aop.aspectj.HystrixCommandAspect;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Servlet;

/**
 * 断路器配置
 *
 * @author yangxin
 * 2020/12/20
 */
@Configuration
public class HystrixConfig {

    /**
     * 用来拦截处理HystrixCommand注解
     */
    @Bean
    public HystrixCommandAspect hystrixAspect() {
        return new HystrixCommandAspect();
    }

    /**
     * 用来向监控中心Dashboard发送stream信息
     */
    @Bean
//    public ServletRegistrationBean hystrixMetricsStreamServlet() {
    public ServletRegistrationBean<Servlet> hystrixMetricsStreamServlet() {
//        ServletRegistrationBean registration = new ServletRegistrationBean();
        ServletRegistrationBean<Servlet> registration = new ServletRegistrationBean<>(new HystrixMetricsStreamServlet());
        registration.addUrlMappings("/hystrix.stream");
        return registration;
    }
}
