package com.technology.user.registration.repositories;

import com.technology.user.registration.models.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, BigInteger> {
    @Query("select u from User u where u.email = :email")
    Optional<User> findUserByEmail(String email);

    @Query("""
            select u from User u
            join u.roles r
            join u.userShifts us
            join u.userActivity ua
            where r.roleName = :roleName
            and us.shiftDate = current_date
            and us.shift.startTime = :shiftStartTime
            and ua.isAvailable = :activityStatus
            """)
    List<User> findAllByRoleNameAndUserShiftStartTimeAndUserActivityStatus(
            @Param("roleName") String roleName,
            @Param("shiftStartTime") LocalTime shiftStartTime,
            @Param("activityStatus") Boolean activityStatus
    );

    @Query("""
            select u from User u
            join u.roles r
            join u.userShifts us
            where r.roleName = :roleName
            and us.shiftDate = current_date
            and us.shift.startTime = :shiftStartTime
            """)
    List<User> findAllUsersByShiftAndRole(@Param("roleName") String roleName,
                                          @Param("shiftStartTime") LocalTime shiftStartTime);

    @Query("""
            select count(u)
            from User u
            join u.roles r
            join u.userShifts us
            join us.shift s
            where r.roleName = :userRole
            and us.shiftDate = current_date
            and s.startTime = :currentShiftStartTime
            """)
    Long findNumberOfUsersByCurrentShiftStartTime(
            @Param("userRole") String userRole,
            @Param("currentShiftStartTime") LocalTime currentShiftStartTime);
}
