package com.technology.order.models;

import lombok.Getter;

@Getter
public enum DeliveryMethod {
    MEEST(10.0),
    COURIER(15.0);

    private final double cost;

    DeliveryMethod(double cost) {
        this.cost = cost;
    }

}
