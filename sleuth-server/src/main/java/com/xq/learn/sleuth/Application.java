package com.xq.learn.sleuth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import zipkin2.server.internal.EnableZipkinServer;

/**
 * @author xiaoqiang
 * @date 2019/10/22 0:33
 */
@SpringBootApplication
@EnableZipkinServer
@EnableDiscoveryClient
public class Application
{
    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }
}
