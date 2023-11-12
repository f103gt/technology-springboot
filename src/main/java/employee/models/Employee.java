package employee.models;

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

    @OneToOne(cascade = CascadeType.MERGE,
            fetch = FetchType.LAZY,
            mappedBy = "employee")
    private Activity employeeActivity;

    @OneToMany(mappedBy = "employees")
    private List<Shift> shifts;
}
