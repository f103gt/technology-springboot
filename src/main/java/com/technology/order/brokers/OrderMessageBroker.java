package com.technology.order.brokers;

import com.technology.config.RabbitMqConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OrderMessageBroker{
    @Value("${rabbitmq.message.queue.order}")
    private String QUEUE;

    @Value("${rabbitmq.employee.topic.order}")
    private String TOPIC;

    @Value("${rabbitmq.binding.key.order}")
    private String BINDING_KEY;


    @Bean
    public Queue orderQueue(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange orderTopicExchange(){
        return new TopicExchange(TOPIC);
    }

    @Bean
    public Binding roderBinding(@Qualifier("orderQueue") Queue queue,
                                @Qualifier("orderTopicExchange") TopicExchange topicExchange){
        return BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(BINDING_KEY);
    }
}
