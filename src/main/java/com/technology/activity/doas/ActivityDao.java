package com.technology.activity.doas;

import com.technology.cart.models.CartItem;
import com.technology.order.models.Order;
import com.technology.role.enums.Role;
import com.technology.shift.models.Shift;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class ActivityDao {
    private final JdbcTemplate jdbcTemplate;
    private static final Logger logger =
            LoggerFactory.getLogger(ActivityDao.class);

    //TODO WHAT IF ALL THE EMPLOYEES OF THE SHIFT ARE ABSENT

    @Transactional
    public String distributeOrder(Order order, Shift shift) {
        int quantity = order.getCart().getCartItems().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();

        String sql1 = """
                SELECT a.id, e.email
                FROM activity a
                INNER JOIN employee e ON a.employee_id = e.id
                INNER JOIN employee_shift es ON e.id = es.employee_id
                INNER JOIN shift s ON es.shift_id = s.id
                WHERE s.start_time = ?
                AND e.role = 'STAFF'
                AND (CURRENT_TIME < s.start_time
                OR a.activity_status IN ('PRESENT','LATE'))
                GROUP BY a.id, e.email
                ORDER BY MIN(a.potential_points) ASC, a.id ASC
                LIMIT 1
            """;

        Map<String, Object> result = jdbcTemplate.queryForMap(sql1, shift.getStartTime());
        String id = result.get("id").toString();
        String email = (String) result.get("email");

        String sql2 = "UPDATE activity a SET potential_points = potential_points + ? WHERE a.id = ?";
        jdbcTemplate.update(sql2, quantity, id);

        String sql3 = "UPDATE shop_order o SET employee_activity_id = ? WHERE o.id = ?";
        jdbcTemplate.update(sql3, id, order.getId());

        return email;
    }


    @Transactional
    public void updateAllEmployeesActivity(Shift shift, Role role) {
        String sql = """
                UPDATE activity
                SET activity_status = CASE
                                    WHEN activity_status <> 'ABSENT'
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
                AND activity_status <> 'ABSENT'
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
