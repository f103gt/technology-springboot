package com.technology.registration.models;

import com.technology.registration.errors.RoleNotFoundException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "client")
@Getter
@Setter
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true)
    private BigInteger id;
    @Column(name = "first_name",nullable = false)
    private String firstName;

    @Column(name = "last_name",nullable = false)
    private String lastName;

    @Column(nullable = false)
    private String patronymic;

    @Column(unique = true,nullable = false)
    private String email;
    @Column(name = "phone_number", nullable = false)
    private String phoneNumber;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_enabled",nullable = false)
    private Boolean isEnabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_role",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "client_address",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "address_id", referencedColumnName = "id"))
    private Set<Address> addresses;


    public static class Builder{
        private final User user;
        private Builder(){
            this.user = new User();
        }

        public Builder firstName(String firstName){
            user.firstName = firstName;
            return this;
        }
        public Builder lastName(String lastName){
            user.lastName = lastName;
            return this;
        }

        public Builder patronymic(String patronymic){
            user.patronymic = patronymic;
            return this;
        }

        public Builder email(String email){
            user.email = email;
            return this;
        }

        public Builder password(String password){
            user.password = password;
            return this;
        }

        public Builder phoneNumber(String phoneNumber){
            user.phoneNumber = phoneNumber;
            return this;
        }

        public Builder isEnabled(Boolean isEnabled){
            user.isEnabled = isEnabled;
            return this;
        }

        public Builder roles(Role role){
            user.roles = Collections.singleton(role);
            return this;
        }

        public Builder addresses(Address address){
            user.addresses = Collections.singleton(address);
            return this;
        }

        public User build(){
            return user;
        }
    }
    public static Builder builder(){
        return new Builder();
    }
}
