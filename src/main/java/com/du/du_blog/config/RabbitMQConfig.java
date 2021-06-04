package com.du.du_blog.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.du.du_blog.constant.RabbitMQPreFixConst;
/**
 * 配置RabbitMQ
 */
@Configuration
public class RabbitMQConfig {

    /**
     * email交换机
     * @return
     */
    @Bean
    public FanoutExchange emailExchange(){
        return new FanoutExchange(RabbitMQPreFixConst.EMAIL_EXCHANGE,true,false);
    }

    /**
     * email队列
     * @return
     */
    @Bean
    public Queue emailQueue(){
        return new Queue(RabbitMQPreFixConst.EMAIL_QUEUE,true);
    }

    /**
     * 绑定交换机和队列
     * @return
     */
    @Bean
    public Binding emailBinding(){
        return BindingBuilder.bind(emailQueue()) .to(emailExchange());
    }
}
