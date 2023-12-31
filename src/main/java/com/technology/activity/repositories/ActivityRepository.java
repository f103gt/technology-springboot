package com.technology.activity.repositories;

import com.technology.activity.models.Activity;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Integer> {
    Optional<Activity> findActivityByEmployeeId(BigInteger employeeId);

    Optional<Activity> findActivityByEmployeeEmail(String employeeEmail);

    @Modifying
    @Query(value = """
            UPDATE Activity a
            SET a.actualPoints = a.actualPoints + :actualPoints
            WHERE a.employee.id = :userId""")
    void changeActualPointsByUserId(@Param("userId") BigInteger userId,
                                    @Param("actualPoints") Integer actualPoints);
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