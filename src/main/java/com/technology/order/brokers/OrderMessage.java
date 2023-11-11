package com.technology.order.brokers;

import com.technology.order.models.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderMessage {
    private Order order;
}
