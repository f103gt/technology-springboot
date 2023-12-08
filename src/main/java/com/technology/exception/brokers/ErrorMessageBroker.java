package com.technology.exception.brokers;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class ErrorMessageBroker {
    @Value("${rabbitmq.message.queue.error}")
    private String QUEUE;

    @Value("${rabbitmq.employee.topic.error}")
    private String TOPIC;

    @Value("${rabbitmq.binding.key.error}")
    private String BINDING_KEY;


    @Bean
    public Queue errorQueue(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange errorTopicExchange(){
        return new TopicExchange(TOPIC);
    }

    @Bean
    public Binding errorBinding(@Qualifier("errorQueue") Queue queue,
                                @Qualifier("errorTopicExchange") TopicExchange topicExchange){
        return BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(BINDING_KEY);
    }
}
