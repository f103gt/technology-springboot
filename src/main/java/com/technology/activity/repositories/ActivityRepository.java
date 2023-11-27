package com.technology.activity.repositories;

import com.technology.activity.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity,Integer> {
    Optional<Activity> findActivityByEmployeeId(BigInteger employeeId);
    Optional<Activity> findActivityByEmployeeEmail(String employeeEmail);
}


/*@Query("""
            select max (a.points) from Activity a
            """)
    Optional<BigInteger> findMaxPoints();
    @Query("""
            select min (a.points) from Activity a
            """)
    Optional<BigInteger> findMinPoints();*/

    /*@Query("""
            select a from Activity a\s
            inner join a.employee u\s
            inner join u.shifts us\s
            where u.role = :roleName\s
            and us.shift = :currentShift\s
            and a.actualPoints < a.potentialPoints
            """)
    List<Activity> findActivitiesByShiftAAndRole(@Param("currentShift") Shift currentShift,
                                                 @Param("role")String role);*/