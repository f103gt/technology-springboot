package com.technology.activity.doas;

import com.technology.activity.models.ActivityStatus;
import com.technology.role.enums.Role;
import com.technology.shift.models.Shift;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.annotation.DirtiesContext;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;


/*@ActiveProfiles("test")
@Import({TestDatabaseConfig.class})
@ContextConfiguration(classes = {TechnologyApplication.class})*/
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@JdbcTest
class ActivityDaoTest {
    private final ActivityDao activityDao;
    private final JdbcTemplate jdbcTemplate;

    public ActivityDaoTest() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.h2.Driver");
        dataSource.setUrl("jdbc:h2:mem:db");
        dataSource.setUsername("as");
        dataSource.setPassword("password");
        jdbcTemplate = new JdbcTemplate(dataSource);
        this.activityDao = new ActivityDao(jdbcTemplate);
    }


    /*private static final Logger logger =
            LoggerFactory.getLogger(ActivityDaoTest.class);*/


    /*TODO method to handle order packing completion -
        setting new order status and updating actual points number*/
    @Test
    @DirtiesContext
    public void updateAllEmployeeActivityTest() {

        //TODO HANDLE THE CASE WHEN THE STATED QUANTITY IS GREATER THEN THE AVAILABLE PRODUCT QUANTITY

        String sqlSetUp = """
                INSERT INTO employee(id, email, role)
                VALUES (1, 'employee1@email.com', 'STAFF');
                                
                INSERT INTO employee(id, email, role)
                VALUES (2, 'employee2@email.com', 'STAFF');
                """;
        jdbcTemplate.update(sqlSetUp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Shift shift1 = Shift.builder()
                .id(1)
                .startTime(LocalDateTime.parse("2023-11-13 10:00:00", formatter))
                .endTime(LocalDateTime.parse("2023-11-13 23:59:59", formatter))
                .build();

        activityDao.updateAllEmployeesActivity(shift1, Role.STAFF);
        String sql = """
                SELECT e.email, activity_status, potential_points, actual_points
                FROM activity a
                INNER JOIN employee e ON a.employee_id = e.id
                INNER JOIN employee_shift es ON a.employee_id = es.employee_id
                INNER JOIN shift s ON es.shift_id = s.id
                WHERE s.start_time = ? AND e.role = ?
                """;
        List<Map<String, Object>> results = jdbcTemplate
                .queryForList(sql, shift1.getStartTime(), Role.STAFF.name());
        assertThat(results.size()).isEqualTo(2);
        results.forEach(
                result -> {
                    assertThat((String) result.get("activity_status")).isEqualTo(ActivityStatus.ABSENT.name());
                    int potentialPoints = (int) result.get("potential_points");
                    int actualPoints = (int) result.get("actual_points");
                    assertThat(potentialPoints).isEqualTo(actualPoints);
                }
        );

    }

    @Test
    @DirtiesContext
    public void updateAllEmployeeActivityTestTestForStaffManager() {

        //TODO HANDLE THE CASE WHEN THE STATED QUANTITY IS GREATER THEN THE AVAILABLE PRODUCT QUANTITY

        String sqlSetUp = """
                INSERT INTO employee(id, email, role)
                VALUES (1, 'employee1@email.com', 'STAFF');
                                
                INSERT INTO employee(id, email, role)
                VALUES (2, 'employee2@email.com', 'MANAGER');
                """;
        jdbcTemplate.update(sqlSetUp);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        Shift shift1 = Shift.builder()
                .id(1)
                .startTime(LocalDateTime.parse("2023-11-13 10:00:00", formatter))
                .endTime(LocalDateTime.parse("2023-11-13 23:59:59", formatter))
                .build();
        activityDao.updateAllEmployeesActivity(shift1, Role.STAFF);
        String testSql = """
                SELECT e.email, activity_status, potential_points, actual_points
                FROM activity a
                INNER JOIN employee e ON a.employee_id = e.id
                INNER JOIN employee_shift es ON a.employee_id = es.employee_id
                INNER JOIN shift s ON es.shift_id = s.id
                WHERE s.start_time = ? AND a.activity_status = 'ABSENT'
                """;
        List<Map<String, Object>> staffEmployees = jdbcTemplate.queryForList(testSql,shift1.getStartTime());
        assertThat(staffEmployees.size()).isEqualTo(1);
        /*activityDao.updateAllEmployeesActivity(shift1, Role.STAFF);
        String sqlUpdated = """
                SELECT e.email, a.activity_status, a.potential_points, a.actual_points
                FROM activity a
                INNER JOIN employee e ON a.employee_id = e.id
                INNER JOIN employee_shift es ON a.employee_id = es.employee_id
                INNER JOIN shift s ON es.shift_id = s.id
                WHERE a.activity_status = 'ABSENT' AND s.start_time = ?
                """;
        List<Map<String, Object>> resultsUpdated = jdbcTemplate.queryForList(sqlUpdated,shift1.getStartTime());
        assertThat(resultsUpdated.size()).isEqualTo(1);
        resultsUpdated.forEach(
                result -> {
                    assertThat((String) result.get("activity_status")).isEqualTo(ActivityStatus.ABSENT.name());
                    int potentialPoints = (int) result.get("potential_points");
                    int actualPoints = (int) result.get("actual_points");
                    assertThat(potentialPoints).isEqualTo(actualPoints);
                }
        );*/
    }

}
/*activityRepository.findById(1)
                .ifPresentOrElse(
                        activity -> {
                            assertThat(activity)
                                    .hasFieldOrPropertyWithValue("activityStatus", ActivityStatus.ABSENT)
                                    .hasFieldOrPropertyWithValue("potentialPoints", activity.getActualPoints());
                        },
                        () -> logger.error("ACTIVITY NOT FOUND")
                );*/


//EMPLOYEE
 /*       Employee employee1 = Employee.builder()
                .id(1)
                .email("employee1@email.com")
                .role(Role.STAFF)
                .build();
        Employee employee2 = Employee.builder()
                .id(2)
                .email("employee2@email.com")
                .role(Role.STAFF)
                .build();
        employeeRepository.save(employee1);
        employeeRepository.save(employee2);
        Activity activity1 = Activity.buil
        der()
                .id(1)
                .activityStatus(ActivityStatus.PRESENT)
                .employee(employee1)
                .potentialPoints(5)
                .actualPoints(0)
                .build();

        Activity activity2 = Activity.builder()
                .id(2)
                .activityStatus(ActivityStatus.PRESENT)
                .employee(employee2)
                .potentialPoints(10)
                .actualPoints(5)
                .build();

        activityRepository.save(activity1);
        activityRepository.save(activity2);

        employee1.setEmployeeActivity(activity1);
        employee2.setEmployeeActivity(activity2);

        employeeRepository.save(employee1);
        employeeRepository.save(employee2);

        *//*activityRepository.saveAll(List.of(activity1,activity2));
        employeeRepository.saveAll(List.of(employee1, employee2));*//*

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        Shift shift1 = Shift.builder()
                .id(1)
                .startTime(LocalDateTime.of(LocalDate.parse(
                        "2023-11-13"), LocalTime.parse("10:00", formatter)))
                .endTime(LocalDateTime.of(LocalDate.parse(
                        "2023-11-13"), LocalTime.parse("23:00", formatter)))
                .employees(List.of(employee1, employee2))
                .build();
        shiftRepository.save(shift1);
        employee1.setShifts(List.of(shift1));
        employee2.setShifts(List.of(shift1));
        employeeRepository.saveAll(List.of(employee1, employee2));*/

/*TESTING WHETHER HOW THE POINTS AND STATUS WILL BE UPDATED*/
/*TESTING HOW THE ORDERS WILL BE DISTRIBUTED*/

        /*Optional<Activity>optional = activityRepository.findActivityById(1);
        if(optional.isPresent()){
            Activity activity = optional.get();
            assertThat(activity)
                    .hasFieldOrPropertyWithValue("employee", employee1);
        }
        activityRepositoryTest.findActivityById(1)
                .ifPresentOrElse(
                        activity -> {
                            assertThat(activity)
                                    .hasFieldOrPropertyWithValue("employee", employee1);
                        },
                        () -> logger.error("ACTIVITY NOT FOUND")
                );

        employeeRepository.findById(1)
                .ifPresentOrElse(
                        employee -> {
                            assertThat(employee)
                                    .hasFieldOrPropertyWithValue("shifts", List.of(shift1));
                        },
                        () -> logger.error("ACTIVITY NOT FOUND")
                );


        shiftRepository.findById(1)
                .ifPresentOrElse(
                        shift -> {
                            assertThat(shift)
                                    .hasFieldOrPropertyWithValue("employees", List.of(employee1,employee2))
                                    .hasFieldOrPropertyWithValue("startTime",LocalDateTime.of(LocalDate.parse(
                                            "2023-11-13"), LocalTime.parse("10:00", formatter)));
                        },
                        () -> logger.error("ACTIVITY NOT FOUND")
                );
        activityDao.updateAllEmployeesActivity(shift1);*/


        /*Optional<Activity>optional = activityRepository.findActivityById(1);
        if(optional.isPresent()){
            Activity activity = optional.get();
            assertThat(activity)
                    .hasFieldOrPropertyWithValue("activityStatus", ActivityStatus.ABSENT)
                    .hasFieldOrPropertyWithValue("potentialPoints", activity.getActualPoints());
        }*/


/*
    @BeforeEach
    @DirtiesContext
    void setUp() {
        User user = User.builder()
                .id(BigInteger.ONE)
                .build();
        userRepository.save(user);
        Product product1 = Product.builder()
                .id(BigInteger.ONE)
                .quantity(10)
                .build();
        Product product2 = Product.builder()
                .id(BigInteger.TWO)
                .quantity(11)
                .build();
        productRepository.saveAll(List.of(product1, product2));

        CartItem cartItem1 = CartItem.builder()
                .id(BigInteger.ONE)
                .product(product1)
                .quantity(2)
                .build();
        CartItem cartItem2 = CartItem.builder()
                .id(BigInteger.TWO)
                .product(product2)
                .quantity(1)
                .build();
        cartItemRepository.saveAll(List.of(cartItem1, cartItem2));
        Cart cart1 = Cart.builder()
                .id(BigInteger.ONE)
                .cartItems(List.of(cartItem1, cartItem2))
                .build();
        Cart cart2 = Cart.builder()
                .id(BigInteger.TWO)
                .cartItems(List.of(cartItem2))
                .build();
        cartRepository.saveAll(List.of(cart1,cart2));
        order1 = Order.builder()
                .id(BigInteger.ONE)
                .user(user)
                .cart(cart1)
                .build();
        order2 = Order.builder()
                .id(BigInteger.TWO)
                .user(user)
                .cart(cart2)
                .build();
        orderRepository.saveAll(List.of(order1, order2));
        cart1.setOrder(order1);
        cart2.setOrder(order2);
        cartRepository.saveAll(List.of(cart1,cart2));
    }*/
