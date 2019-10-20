package com.xq.learn.rabbit.config;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Method;
import com.rabbitmq.client.ShutdownSignalException;
import java.util.concurrent.ThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ChannelListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionNameStrategy;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.SimplePropertyValueConnectionNameStrategy;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

/**
 * Rabbit配置类，主要配置connectionFactory和rabbitTemplate，指定消息投递失败时的处理方式
 * @author xiaoqiang
 * @date 2019/9/20 1:27
 */
@Configuration
public class RabbitConfig
{
    private static final Logger logger = LoggerFactory.getLogger(RabbitConfig.class);

    /**
     * 通过bean设置connectionFactory，以后使用时直接可以在配置文件中设置
     * 但是考虑到企业中配置文件不能有明文密码，因此还需要在bean中配置属性，将密文密码在此解密使用，使用bean注入
     * 的场景更多。
     * @param hostName rabbitmq主机名称
     * @return
     */
    @Bean
    public ConnectionFactory connectionFactory(@Value("${spring.rabbitmq.host}") String hostName,
                                               ThreadFactory threadFactory, ConnectionNameStrategy connectionNameStrategy)
    {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(hostName);
        // 设置每个connection可以缓存的channel大小，需要注意的是缓存channel的大小不是限制可以使用的channel，当缓存
        // 为50时，可以使用的channel是无限制的，只是可以重复利用的有50个，多余的部分使用完毕之后自动关闭。当缓存设置的过小时，
        // 如果是一个高负载的mq，channel的创建和关闭频率更高，通过调整cache大小避免这种现象。
        connectionFactory.setChannelCacheSize(50);
        // 当该值>0时，上面的channelCacheSize就成了限制，一个连接最多可以由50个channel，当channel达到最大值时，获
        // 取channel进入等待状态，超过等待超时时间时，会报错AmqpTimeoutException。
        connectionFactory.setChannelCheckoutTimeout(10000);
        // 设置连接的线程工程类，主要设置线程的名称，便于问题定位，这里使用spring提供的自定义thread-factory
        connectionFactory.setConnectionThreadFactory(threadFactory);
        // 设置连接命名的策略，在管理控制台可以区分不同的微服务
        connectionFactory.setConnectionNameStrategy(connectionNameStrategy);
        // 配置连接超时时间
        connectionFactory.setConnectionTimeout(1000);
        // 发布者确认消息是否成功发送到exchange, 如果指定的交换机不存在也会调用confirms
        connectionFactory.setPublisherConfirms(true);
        // 发布者返回消息：这种情况主要是处理消息已经发布到exchange，但是没有与该exchange绑定的queue的异常情况
        // 要使用publisherReturns，rabbitTemplate的mandatory属性必须为true，或者mandatory-expression表达式的
        // 计算结果为true，而且rabbitTemplate需要注册returnCallback
        connectionFactory.setPublisherReturns(true);
        // 如果发布者发布的消息指定的exchange不存在，channel会关闭，则不会收到publisherReturns, 可以配置
        // ChannelListener来监听channel的关闭动作，分析原因
        connectionFactory.addChannelListener(new ChannelListener(){

            @Override
            public void onCreate(Channel channel, boolean transactional)
            {
                logger.info("onCreate.");
            }

            @Override
            public void onShutDown(ShutdownSignalException signal)
            {
                // 通过分析reason，可以分析channel关闭的原因。
                Method reason = signal.getReason();
                logger.info("onShutDown: " + reason);
            }
        });
        // 设置用户名
        connectionFactory.setUsername("admin");
        // 设置密码
        connectionFactory.setPassword("xiaoqiang");
        return connectionFactory;
    }

    /**
     * 注入ThreadFactory，目的是为connection中的线程配置名称前缀
     * @param threadPrefix 线程名称前缀
     * @return threadFactory
     */
    @Bean
    public CustomizableThreadFactory threadFactory(@Value("${rabbit.connection.thread.prefix}")String threadPrefix)
    {
        return new CustomizableThreadFactory(threadPrefix);
    }

    /**
     * 注入Connection命名策略，指定的属性名称必须在application.properties配置文件中存在，即在Environment
     * @return connectionNameStrategy bean
     */
    @Bean
    public ConnectionNameStrategy connectionNameStrategy()
    {
        return new SimplePropertyValueConnectionNameStrategy("spring.application.name");
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory)
    {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        /*
         * 当connectionFactory开启了publisherReturns时，需要提供returnCallback函数，一个rabbitTemplate只
         * 支持一个ReturnCallback。rabbitTemplate必须开启Mandatory才会调用，return的时机是交换机将信息投递到
         * 消息队列时才会调用，因此要是指定的交换机的名称不存在，则不会调用returnCallback.
         * Mandatory的含义：
         * 1. true 表示当交换机无法根据自身的类型和路由键找到一个符合条件的队列时，那么RabbitMQ会调用Basic.Return
         * 命令将消息返回给生产者，生产者通过ReturnListener来监听没有被正确路由到消息队列中的消息。
         * 2. false 表示当交换机无法根据自身的类型和路由键找到一个符合条件的队列时，那么Rabbit会丢弃该消息
         */
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) ->
        {
            logger.info("<<<returnCallback>>>");
            logger.info("message: " + message);
            logger.info("replyCode: {}, replyText: {}, exchange: {}, routingKey: {}", replyCode, replyText, exchange, routingKey);
        });
        /*
         * 当connectionFactory开启了publisherConfirms时，需要注册一个ConfirmCallback, 一个rabbitTemplate只
         * 支持一个ConfirmCallback。当指定的交换机不存在时也会调用
         * 发布者确认机制只保证消息被成功投递到了指定类型的交换机上面，但是不保证交换机将消息投递到了合适的队列中，因此当
         * 指定的交换机无法根据路由将消息投递到消息队列的时候需要使用重新发送消息，解决方案有两种：
         * 1. 使用Mandatory和publishReturn机制将接受RabbitMQ返回的消息（RabbitMQ会将未成功投机的消息方法给publish）
         * 2. 使用备份交换机（Alternate-Exchange）,声明交换机时通过参数指定alternate-exchange的名称，此时RabbitMQ会
         * 将未成功投递的消息发送到备份交换机上面，备份交换机建议声明为Fanout类型的交换机。
         */
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) ->
        {
            String id = correlationData.getId();
            String message = correlationData.getReturnedMessage().toString();
            logger.info("<<<confirmCallback>>>");
            logger.info("id: {}, message: {}", id, message);
            logger.info("ack: " + ack);
            logger.info("cause: " + cause);
        });

        return rabbitTemplate;
    }
}
