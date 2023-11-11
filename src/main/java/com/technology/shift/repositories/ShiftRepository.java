package com.technology.shift.repositories;

import com.technology.shift.models.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

public interface ShiftRepository extends JpaRepository<Shift, Integer> {
    Optional<Shift> findShiftByStartTime(LocalTime startTime);

    @Query("""
            select s from Shift s
            where current_time between s.startTime and s.endTime
            """)
    Optional<Shift> findShiftByCurrentTime();

    @Query(value= """
            select s from Shift s\s
            where s.endTime > current_timestamp\s
            order by s.endTime asc
            """)
    Optional<Shift> findShiftClosestToCurrentTime();

    @Query(value= """
            select max(s.endTime) from Shift s\s
            where date(s.endTime) = :date
                        """)
   Optional <LocalDateTime> findEndOfTheLatestShift(@Param("date") LocalDate date);

}
