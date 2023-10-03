package com.technology.order.exceptions;

import com.technology.exception.general.exceptions.ObjectNotFoundException;

public class OrderNotFoundException extends ObjectNotFoundException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
