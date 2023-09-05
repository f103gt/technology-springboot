package com.technology.registration.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.type.descriptor.sql.internal.CapacityDependentDdlType;

import java.math.BigInteger;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private BigInteger id;
    @Column(nullable = false)
    private String region;
    @Column(nullable = false)
    private String district;
    //city town
    @Column(nullable = false)
    private String locality;
    @Column(nullable = false)
    private String street;
    @Column(nullable = false)
    private String premise;
    @Column(nullable = false)
    private String zipcode;

    @ManyToMany(mappedBy = "addresses")
    private Set<User> users;

    public Address(String region, String district, String locality, String street, String premise, String zipcode) {
        this.region = region;
        this.district = district;
        this.locality = locality;
        this.street = street;
        this.premise = premise;
        this.zipcode = zipcode;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Address address;
        private Builder(){
            this.address = new Address();
        }

        public Builder setRegion(String region){
            address.region = region;
            return this;
        }

        public Builder setDistrict(String district){
            address.district = district;
            return this;
        }

        public Builder setLocality(String locality){
            address.locality = locality;
            return this;
        }

        public Builder setPremiste(String premise){
            address.premise = premise;
            return this;
        }

        public Builder setZipcode(String zipcode){
            address.zipcode = zipcode;
            return this;
        }
        public Address build(){
            return address;
        }
    }
}
