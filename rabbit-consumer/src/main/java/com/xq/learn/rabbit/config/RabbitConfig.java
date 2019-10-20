package com.xq.learn.rabbit.config;

import java.util.concurrent.ThreadFactory;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * Rabbit配置
 * @author xiaoqiang
 * @date 2019/10/20 17:43
 */
@Configuration
public class RabbitConfig
{
    /**
     * rabbitmq连接工场bean
     * @param hostName 主机名称
     * @param username 用户名
     * @param password 密码
     * @param threadFactory connection线程工场
     * @param nameStrategy connection命名策略
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String hostName,
                                               @Value("${spring.rabbitmq.username}") String username,
                                               @Value("${spring.rabbitmq.password}") String password,
                                               @Value("${spring.rabbitmq.cache.channel.size}") int cacheChanelSize,
                                               ThreadFactory threadFactory, ConnectionNameStrategy nameStrategy)
    {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(hostName);
        connectionFactory.setConnectionThreadFactory(threadFactory);
        connectionFactory.setConnectionNameStrategy(nameStrategy);
        connectionFactory.setChannelCacheSize(cacheChanelSize);
        connectionFactory.setChannelCheckoutTimeout(10000);
        connectionFactory.setConnectionTimeout(1000);
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CHANNEL);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);

        return connectionFactory;
    }

    /**
     * threadFactory bean，用于创建connection线程时，指定固定的前缀
     * @param threadPrefix 线程名称前缀
     * @return threadFactory bean
     */
    @Bean
    public CustomizableThreadFactory threadFactory(@Value("${rabbit.connection.thread.prefix}")String threadPrefix)
    {
        return new CustomizableThreadFactory(threadPrefix);
    }

    /**
     * connection命名策略
     * @return nameStrategy bean
     */
    @Bean
    public ConnectionNameStrategy nameStrategy()
    {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

    /**
     * 注入rabbitTemplate bean，用于rabbit的所有操作
     * @param connectionFactory 连接工场
     * @return rabbitTemplate bean
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory)
    {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        return rabbitTemplate;
    }

    /**
     * 配置containerFactory, 与@RabbitListener注解使用时，可以指定containerFactory
     * @param connectionFactory 连接工场
     * @return containerFactory
     */
    @Bean
    public RabbitListenerContainerFactory containerFactory(ConnectionFactory connectionFactory)
    {
        SimpleRabbitListenerContainerFactory containerFactory = new SimpleRabbitListenerContainerFactory();
        containerFactory.setConnectionFactory(connectionFactory);
        // 设置手动应答，需要在接受到消息时手动应答消息，否则会一值等待消息应答
        containerFactory.setAcknowledgeMode(AcknowledgeMode.MANUAL);
        // 设置最小的消费者线程数量
        containerFactory.setConcurrentConsumers(2);
        // 设置最大的消费者线程数量
        containerFactory.setMaxConcurrentConsumers(10);
        // 预拉取消息数量
        containerFactory.setPrefetchCount(5);

        return containerFactory;
    }

}
