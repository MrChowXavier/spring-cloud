package com.xq.learn.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

@EnableEurekaServer
@SpringBootApplication
public class Application extends WebSecurityConfigurerAdapter
{

    public static void main(String[] args)
    {
        SpringApplication.run(Application.class, args);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception
    {
        // 2.0版本默认开启了csrf防护，需要禁用
        http.csrf().disable();
        super.configure(http);
    }
}
