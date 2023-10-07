package com.technology.role.models;

import com.technology.user.models.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
public class Role{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_name",unique = true)
    private String roleName;

    @ManyToMany(mappedBy = "roles")
    private Set<User> users;
}
