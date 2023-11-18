package com.technology.employee.models;

import com.technology.activity.models.Activity;
import com.technology.role.enums.Role;
import com.technology.shift.models.Shift;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //@Column(unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL,
            fetch = FetchType.EAGER,
            mappedBy = "employee")
    private Activity employeeActivity;

    @Column(name = "is_registered")
    private boolean isRegistered;

    @ManyToMany(mappedBy = "employees")
    private List<Shift> shifts;
}
