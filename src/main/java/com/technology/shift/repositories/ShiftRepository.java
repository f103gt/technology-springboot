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
        @Query("""
                select s from Shift s
                where local_datetime between
                cast(s.startTime as timestamp ) and cast(s.endTime as timestamp)
                """)
        Optional<Shift> findShiftByCurrentTime();

    @Query(value= """
            select s from Shift s
            where cast(s.endTime as timestamp) > current_timestamp
            order by s.endTime asc
            """)
    Optional<Shift> findShiftClosestToCurrentTime();

    @Query(value= """
            select max(s.endTime) from Shift s\s
            where date(s.endTime) = :date
                        """)
   Optional <LocalDateTime> findEndOfTheLatestShift(@Param("date") LocalDate date);

}
