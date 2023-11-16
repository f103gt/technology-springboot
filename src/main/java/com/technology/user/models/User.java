package com.technology.user.models;

import com.technology.activity.models.Activity;
import com.technology.cart.models.Cart;
import com.technology.order.models.Order;
import com.technology.role.enums.Role;
import com.technology.security.jwt.models.Token;
import com.technology.shift.models.Shift;
import com.technology.validation.otp.models.Otp;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigInteger;
import java.util.List;

@Entity
@Table(name = "client")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(unique = true)
    private String email;

    private String password;

    @Column(name = "is_enabled")
    private Boolean isEnabled;

    @Enumerated(EnumType.STRING)
    private Role role;

    @OneToOne(cascade = CascadeType.ALL,
            mappedBy = "user",fetch = FetchType.LAZY)
    private Cart cart;

/*
    @OneToOne(cascade = CascadeType.MERGE,
            fetch = FetchType.LAZY,
    mappedBy = "employee")
    private Activity employeeActivity;
*/

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    //TODO consider moving shifts to employee activity

   /* @ManyToMany(mappedBy = "employees")
    private List<Shift> shifts;
*/
    @OneToMany(fetch = FetchType.LAZY,cascade =
            {CascadeType.MERGE,CascadeType.PERSIST,CascadeType.REFRESH})
    private List<Order> orders;


    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Otp> otps;
}

//TODO test whether now address will be saved automatically when i save it in user
//TODO instead of saving address first and only then a user entity


 /* @ManyToMany(fetch = FetchType.EAGER,cascade = CascadeType.ALL)
    @JoinTable(name = "client_address",
            joinColumns = @JoinColumn(name = "client_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "address_id", referencedColumnName = "id"))
    private Set<Address> addresses;*/
/*
public static class Builder{
        private final User user;
        private Builder(){
            this.user = new User();
        }

        public Builder id(BigInteger id){
            user.id = id;
            return this;
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
 */