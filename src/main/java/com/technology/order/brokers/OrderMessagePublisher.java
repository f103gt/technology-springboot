package com.technology.order.brokers;

import com.technology.order.models.Order;
import com.technology.user.brokers.CustomMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderMessagePublisher {
    @Value("${rabbitmq.employee.topic.order}")
    private String orderTopic;

    @Value("${rabbitmq.binding.key.order}")
    private String orderBindingKey;

    private final RabbitTemplate rabbitTemplate;

    public void publishMessage(OrderMessage order) {
        rabbitTemplate.convertAndSend(
                orderTopic,
                orderBindingKey,
                order
        );
    }
}
