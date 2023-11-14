package com.technology.activity.doas;

import com.technology.activity.models.ActivityStatus;
import com.technology.order.models.Order;
import com.technology.role.enums.Role;
import com.technology.shift.models.Shift;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ActivityDao {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger =
            LoggerFactory.getLogger(ActivityDao.class);

    @Transactional
    public void distributeOrder(Order order) {
        String sql = "UPDATE activity a " +
                "SET potential_points = potential_points + ? " +
                "FROM employee e" +
                "WHERE a.employee_id = e.id " +
                "AND e.role = ? " +
                "AND (e.activity_status = ? OR e.activity_status = ?) " +
                "AND potential_points = (SELECT MIN(potential_points) " +
                "FROM activity WHERE user_id = id " +
                "ORDER BY id LIMIT 1)";
        jdbcTemplate.update(sql, order.getCart().getCartItems().size(),
                Role.STAFF.name(), ActivityStatus.PRESENT, ActivityStatus.LATE);
    }

    @Transactional
    public void updateAllEmployeesActivity(Shift shift, Role role) {
        String sql = """
                UPDATE activity
                SET activity_status = 'ABSENT',
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
