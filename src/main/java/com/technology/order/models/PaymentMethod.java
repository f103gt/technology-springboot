package com.technology.order.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

public enum PaymentMethod {
    CARD,
    CASH,
    PAYPAL
}
