package com.technology.shift.repositories;

import com.technology.shift.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalTime;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    Optional<Shift> findShiftByStartTime(LocalTime startTime);

    @Query("""
            select s from Shift s
            where current_time between s.startTime and s.endTime
            """)
    Optional<Shift> findShiftByCurrentTime();
}
