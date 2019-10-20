package com.xq.learn.rabbit.consumer;

import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * 消息消费者
 * @author xiaoqiang
 * @date 2019/10/20 13:53
 */
@Component
public class MessageConsumer
{
    private static final Logger logger = LoggerFactory.getLogger(MessageConsumer.class);

    @RabbitListener(bindings = {@QueueBinding(value = @Queue("direct_queue"),
            exchange = @Exchange(value = "direct"), key = "direct_route_key"),
            @QueueBinding(value = @Queue("direct_queue_1"),
                    exchange = @Exchange(value = "direct"), key = "direct_route_key")},
            containerFactory = "containerFactory")
    public void consumer(Message message, Channel channel) throws Exception
    {
        logger.info("consumer: " + message);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        channel.basicAck(deliveryTag, true);
    }
}
