package com.technology.user.models;

import com.technology.role.enums.Role;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Entity
@Table(name = "new_employee")
public class NewEmployee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //@Column(unique = true)
    private String email;
    @Enumerated(EnumType.STRING)
    private Role role;
    @Column(name = "is_registered")
    private boolean isRegistered;
    @Column(name = "file_hash")
    private String fileHash;
}
