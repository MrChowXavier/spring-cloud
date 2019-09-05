package com.xq.learn.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;

/**
 * EnableZuulProxy注解的作用是使用zuul做为路由处理，默认情况下当项目中依赖了spring boot actuator时，会有
 * 下面两个地址：
 * /routes
 * /filters
 */
@EnableZuulProxy
@EnableDiscoveryClient
@SpringBootApplication
public class Application
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

}
