package com.technology.user.registration.repositories;

import com.technology.user.registration.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, BigInteger> {
    @Query("select u from User u where u.email = :email")
    Optional<User> findUserByEmail(String email);

}
