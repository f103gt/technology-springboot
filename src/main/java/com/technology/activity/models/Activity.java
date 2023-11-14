package com.technology.activity.models;

import com.technology.order.models.Order;
import com.technology.employee.models.Employee;
import jakarta.persistence.*;
import lombok.*;

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
    private Integer id;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER)
    @JoinColumn(name = "employee_id",referencedColumnName = "id")
    private Employee employee;

    @Enumerated(EnumType.STRING)
    private ActivityStatus activityStatus;

    @OneToMany(mappedBy = "employeeActivity",
            fetch = FetchType.EAGER,
            cascade = CascadeType.MERGE)
    private List<Order> orders;

    @Column(name = "potential_points")
    private Integer potentialPoints;

    @Column(name = "actual_points")
    private Integer actualPoints;
}
