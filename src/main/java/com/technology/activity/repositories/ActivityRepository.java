package com.technology.activity.repositories;

import com.technology.activity.models.Activity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigInteger;
import java.util.Optional;

public interface ActivityRepository extends JpaRepository<Activity,BigInteger> {
    @Query("""
            select max (a.points) from Activity a
            """)
    Optional<BigInteger> findMaxPoints();
    @Query("""
            select min (a.points) from Activity a
            """)
    Optional<BigInteger> findMinPoints();
}
