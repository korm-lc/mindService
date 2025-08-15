package org.xaut.voicemindserver.configure;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String QUEUE_NAME = "audio_task_queue";
    public static final String EXCHANGE_NAME = "audio_task_exchange";
    public static final String ROUTING_KEY = "audio_task_routing";

    // 队列
    @Bean
    public Queue audioTaskQueue() {
        return new Queue(QUEUE_NAME, true);
    }

    // 交换机
    @Bean
    public DirectExchange audioTaskExchange() {
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    // 队列绑定交换机
    @Bean
    public Binding bindingAudioQueue(Queue audioTaskQueue, DirectExchange audioTaskExchange) {
        return BindingBuilder.bind(audioTaskQueue).to(audioTaskExchange).with(ROUTING_KEY);
    }

    // JSON 消息转换器
    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    // RabbitTemplate 使用 JSON 转换器
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter());
        return template;
    }

}
