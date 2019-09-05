package com.xq.learn.client.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoqiang
 * @date 2019/9/5 3:16
 */
@RestController
@RequestMapping("v1/cloud")
public class DemoController
{
    @Value("${server.port}")
    private int port;

    @RequestMapping(value = "port", method = RequestMethod.GET)
    public String serverDiscovery()
    {
        return "I'm " + port;
    }
}
