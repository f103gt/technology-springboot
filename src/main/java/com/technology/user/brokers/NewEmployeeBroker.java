package com.technology.user.brokers;

import com.technology.config.RabbitMqConfig;
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
public class NewEmployeeBroker{
    @Value("${rabbitmq.message.queue}")
    private String QUEUE;

    @Value("${rabbitmq.employee.topic}")
    private String TOPIC;

    @Value("${rabbitmq.binding.key}")
    private String BINDING_KEY;

    @Bean
    public Queue newEmployeeQueue(){
        return new Queue(QUEUE);
    }

    @Bean
    public TopicExchange newEmployeeTopicExchange(){
        return new TopicExchange(TOPIC);
    }

    @Bean
    public Binding newEmployeeBinding(@Qualifier("newEmployeeQueue") Queue queue,
                                      @Qualifier("newEmployeeTopicExchange") TopicExchange topicExchange){
        return BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(BINDING_KEY);
    }
}
