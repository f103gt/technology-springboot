package com.technology.order.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table("order_status")
public class OrderStatus {
    private Integer id;
    private String status;
}
