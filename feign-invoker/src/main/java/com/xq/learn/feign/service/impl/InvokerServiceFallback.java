package com.xq.learn.feign.service.impl;

import com.xq.learn.dto.User;
import com.xq.learn.feign.service.InvokerService;
import org.springframework.stereotype.Service;

/**
 * 这是一个容错机制，当服务提供者不能访问时，使用该方法作为异常处理，是备份方案
 * @author xiaoqiang
 * @date 2019/10/15 2:53
 */
@Service
public class InvokerServiceFallback implements InvokerService
{
    @Override
    public String demo(String name, int age)
    {
        return "demo fallback: " + name;
    }

    @Override
    public String body(User user)
    {
        return "body fallback: userName: " + user.getUserName();
    }
}
