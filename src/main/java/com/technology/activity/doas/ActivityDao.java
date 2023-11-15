package com.technology.activity.doas;

import com.technology.order.models.Order;
import com.technology.role.enums.Role;
import com.technology.shift.models.Shift;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActivityDao {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger =
            LoggerFactory.getLogger(ActivityDao.class);

    //TODO WHAT IF ALL THE EMPLOYEES OF THE SHIFT ARE ABSENT
    @Transactional
    public void distributeOrderClosestShift(Order order,Shift shift){
        String sql = """
                WITH min_potential_points AS(
                    SELECT MIN(potential_points) FROM activity a
                    INNER JOIN employee e ON a.employee_id = e.id
                    INNER JOIN employee_shift es ON e.id = es.employee_id
                    INNER JOIN shift s ON es.shift_id = s.id
                    WHERE s.start_time = ?
                    AND e.role = 'STAFF'
                )
                UPDATE activity a
                INNER JOIN employee e ON a.employee_id = e.id
                INNER JOIN employee_shift ON e.id = es.employee_id
                INNER JOIN shift s ON es.shift_id = s.id
                WHERE s.start_time = ?
                AND e.role = 'STAFF'
                AND a.potential_points = min_potential_points
                ORDER BY a.id LIMIT 1
                """;
        jdbcTemplate.update(sql, order.getCart().getCartItems().size(), shift.getStartTime());
    }

    @Transactional
    public void distributeOrder(Order order,Shift shift) {
        String sql = """
                WITH min_potential_points AS(
                    SELECT MIN(potential_points) FROM activity a
                    INNER JOIN employee e ON a.employee_id = e.id
                    INNER JOIN employee_shift es ON e.id = es.employee_id
                    INNER JOIN shift s ON es.shift_id = s.id
                    WHERE s.start_time = ?
                    AND e.role = 'STAFF'
                    AND a.activity_status IN ('PRESENT','LATE')
                )
                UPDATE activity a
                INNER JOIN employee e ON a.employee_id = e.id
                INNER JOIN employee_shift ON e.id = es.employee_id
                INNER JOIN shift s ON es.shift_id = s.id
                WHERE s.start_time = ?
                AND e.role = 'STAFF'
                AND a.activity_status IN ('PRESENT','LATE')
                AND a.potential_points = min_potential_points
                ORDER BY a.id LIMIT 1
                """;

        int rowsAffected = jdbcTemplate.update(sql, order.getCart().getCartItems().size(), shift.getStartTime());
        if(rowsAffected == 0){
            distributeOrderClosestShift(order,shift);
        }
    }

    @Transactional
    public void updateAllEmployeesActivity(Shift shift, Role role) {
        String sql = """
                UPDATE activity
                SET activity_status = CASE
                                    WHEN activity_stas <> 'ABSENT'
                                    THEN 'ABSENT'
                                    ELSE activity_status
                                    END,
                    potential_points = CASE
                                        WHEN potential_points > actual_points
                                        THEN actual_points
                                        ELSE potential_points
                                        END
                WHERE employee_id IN(
                    SELECT es.employee_id
                    FROM employee_shift es
                    INNER JOIN employee e
                    ON es.employee_id = e.id
                    INNER JOIN shift s
                    ON es.shift_id = s.id
                    WHERE s.start_time = ?
                    AND e.role = ?
                )      
                """;
        jdbcTemplate.update(sql, shift.getStartTime(), role.name());

    }

}



    /*
 String subquery = """
                    SELECT employee_id
                    FROM employee_shift
                    WHERE shift_id IN (
                        SELECT id
                        FROM shift
                        WHERE start_time = ?
                    )
                """;
        List<Integer> employeeIds = jdbcTemplate.queryForList(subquery, Integer.class, shift.getStartTime());
        logger.info("Subquery output: " + employeeIds);    //TODO add check up for the role

    //TODO update status for the employees whose shift start time equals
    //TODO  to the current shift start time, set status ABSENT
    //update all employees with current status present or late with the status absent
    //TODO FIX THE EMPLOYEE-ACTIVITY RELATION TO BE MANY-TO-MANY
}*/
