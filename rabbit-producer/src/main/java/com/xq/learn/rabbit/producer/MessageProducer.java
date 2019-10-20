package com.xq.learn.rabbit.producer;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author xiaoqiang
 * @date 2019/10/20 11:38
 */
@Component
public class MessageProducer
{
    private final static Logger logger = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void send(String message)
    {
        CorrelationData correlationData = new CorrelationData();
        String id = UUID.randomUUID().toString();
        correlationData.setId(id);
        correlationData.setReturnedMessage(new Message("success".getBytes(), null));
        logger.info("<<<<<id: {}", id);
        // 消息投递失败时，设置手动处理而不是直接将消息丢弃
        rabbitTemplate.setMandatory(true);

        rabbitTemplate.convertAndSend("direct","direct_route_key", message, correlationData);
    }
}
