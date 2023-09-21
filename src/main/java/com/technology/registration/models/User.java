package com.technology.registration.models;

import com.technology.cart.models.Cart;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigInteger;
import java.util.Collections;
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

    @Column(unique = true,nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "is_enabled",nullable = false)
    private Boolean isEnabled;

    //TODO test the same for role
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "client_role",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"))
    private Set<Role> roles;

    //TODO test whether now address will be saved automatically when i save it in user
    //TODO instead of saving address first and only then a user entity
    @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "client_address",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "address_id", referencedColumnName = "id"))
    private Set<Address> addresses;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "cart_id")
    private Cart cart;

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

        public Builder email(String email){
            user.email = email;
            return this;
        }

        public Builder password(String password){
            user.password = password;
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
