package com.technology.order.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "delivery_method")
public class DeliveryMethod {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "delivery_method_name")
    private String deliveryMethodName;

    @OneToMany(mappedBy = "deliverMethod")
    private Collection<Order> orders;
}
