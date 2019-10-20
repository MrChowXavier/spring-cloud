package com.xq.learn.rabbit.config;

import org.springframework.amqp.core.DirectExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 消息配置类，配置交换机类型以及与交换机绑定的队列信息
 * @author xiaoqiang
 * @date 2019/10/20 13:28
 */
@Configuration
public class MessageConfig
{
    /**
     * direct类型的交换机， 生产者只需要将消息发送到交换机
     * @return
     */
    @Bean
    public DirectExchange directExchange()
    {
        return new DirectExchange("direct", true, false);
    }

}
