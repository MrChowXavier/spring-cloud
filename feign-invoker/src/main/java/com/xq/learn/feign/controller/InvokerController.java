package com.xq.learn.feign.controller;

import com.xq.learn.feign.service.InvokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiaoqiang
 * @date 2019/9/5 4:12
 */
@RestController
@RequestMapping("feign")
public class InvokerController
{
    @Autowired
    private InvokerService invokerService;

    @RequestMapping("port")
    public String invokerService()
    {
        return invokerService.demo();
    }
}
