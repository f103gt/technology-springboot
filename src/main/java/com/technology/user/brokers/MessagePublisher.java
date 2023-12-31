package com.technology.user.brokers;

import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MessagePublisher {
    @Value("${rabbitmq.employee.topic}")
    private String employeeTopic;

    @Value("${rabbitmq.binding.key}")
    private String employeeBindingKey;
    private final RabbitTemplate rabbitTemplate;

    public void publishMessage(List<Object[]> message) {
        rabbitTemplate.convertAndSend(
                employeeTopic,
                employeeBindingKey,
                message
        );
    }
}
