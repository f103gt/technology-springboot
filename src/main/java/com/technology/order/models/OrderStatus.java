package com.technology.order.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;
    public enum OrderStatus {
        PENDING,
        PACKED,
        SENT,
        DELIVERED
    }
