package com.xq.learn.feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * 使用Feign做客户端负载均衡，需要用到@FeignClient注解，该注解中提供服务注册中心的服务名称
 * 当访问该接口时，使用rpc调用服务提供者接口
 * @author xiaoqiang
 * @date 2019/9/5 4:06
 */
@FeignClient("server-provider")
@Service
public interface InvokerService
{
    @RequestMapping(value = "/v1/cloud/port", method = RequestMethod.GET)
    String demo();
}
