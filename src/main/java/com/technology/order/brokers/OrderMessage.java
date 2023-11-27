package com.technology.order.brokers;

import com.technology.order.models.Order;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigInteger;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderMessage {
    private BigInteger orderId;
    private int quantity;
    private String messageBody;
}
