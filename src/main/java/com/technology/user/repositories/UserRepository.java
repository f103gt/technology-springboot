package com.technology.user.repositories;

import com.technology.user.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, BigInteger> {
    @Query("select u from User u where u.email = :email")
    Optional<User> findUserByEmail(String email);

   /* @Query("""
            select u from User u\s
            inner join u.shifts us\s
            inner join u.employeeActivity ua\s
            where u.role = :roleName\s
            and us.startTime = :shiftStartTime\s
            and ua.activityStatus = 'PRESENT' or ua.activityStatus = 'LATE'
            """)
    Optional<User> findUserByShiftAndActivityStatus(@Param("roleName")String roleName,
                                                    @Param("shiftStartTime")LocalDateTime shiftStartTime);*/
}
