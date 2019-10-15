package com.xq.learn.feign.service;

import com.xq.learn.dto.User;
import com.xq.learn.feign.service.impl.InvokerServiceFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 使用Feign做客户端负载均衡，需要用到@FeignClient注解，该注解中提供服务注册中心的服务名称
 * 当访问该接口时，使用rpc调用服务提供者接口
 * Feign默认继承了Hystrix熔断机制，fallback参数指定备用方法所在的类，该类实现了需要调用的方法，当
 * 服务提供者不可用时，提供了一种异常处理机制。
 * @author xiaoqiang
 * @date 2019/9/5 4:06
 */
@FeignClient(value = "server-provider", fallback = InvokerServiceFallback.class)
@Service
public interface InvokerService
{
    /**
     * 使用RequestParam注解传参
     * @param name 名称
     * @param age 年龄
     * @return demo
     */
    @RequestMapping(value = "/v1/cloud/port", method = RequestMethod.GET)
    String demo(@RequestParam("name") String name, @RequestParam("age") int age);

    /**
     * 使用RequestBody传参
     * @param user user对象
     * @return demo
     */
    @RequestMapping(value = "/v1/cloud/body", method = RequestMethod.POST)
    String body(@RequestBody User user);
}
