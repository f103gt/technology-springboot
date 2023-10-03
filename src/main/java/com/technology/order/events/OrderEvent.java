package com.technology.order.events;

import com.technology.order.models.Order;
import org.springframework.context.ApplicationEvent;

import java.time.Clock;

public class OrderEvent extends ApplicationEvent {
    private Order order;

    public OrderEvent(Object source, Order order) {
        super(source);
        this.order = order;
    }

    public Order getOrder() {
        return order;
    }
}
