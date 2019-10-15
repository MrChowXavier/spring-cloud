package com.xq.learn.feign.controller;

import com.xq.learn.dto.User;
import com.xq.learn.feign.service.InvokerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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

    @RequestMapping(value = "port", method = RequestMethod.GET)
    public String invokerService(@RequestParam("name") String name, @RequestParam("age") int age)
    {
        return invokerService.demo(name, age);
    }

    @RequestMapping(value = "body", method = RequestMethod.POST)
    public String body(@RequestBody User user)
    {
        return invokerService.body(user);
    }
}
