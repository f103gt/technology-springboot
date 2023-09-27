package com.technology.address.repositories;

import com.technology.address.models.Address;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;

public interface AddressRepository extends JpaRepository<Address, BigInteger> {
}
