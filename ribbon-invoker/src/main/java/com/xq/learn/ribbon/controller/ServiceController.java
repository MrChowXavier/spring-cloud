package com.xq.learn.ribbon.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

/**
 * @author xiaoqiang
 * @date 2019/9/6 1:33
 */
@RestController
@RequestMapping("ribbon")
public class ServiceController
{
    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("port")
    public String invokerService()
    {
        // 直接使用服务名称进行访问
        return restTemplate.getForObject("http://server-provider/v1/cloud/port", String.class);
    }
}
