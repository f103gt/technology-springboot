package com.technology.activity.models;

import com.technology.order.models.Order;
import com.technology.user.models.User;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Activity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @OneToOne(cascade = CascadeType.MERGE,
            fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id",referencedColumnName = "id")
    private User employee;

    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;

    @OneToMany(mappedBy = "employeeActivity",
            fetch = FetchType.LAZY,
            cascade = CascadeType.MERGE)
    private List<Order> orders;

    @Column(name = "potential_points")
    private Integer potentialPoints;

    @Column(name = "actual_points")
    private Integer actualPoints;
}
