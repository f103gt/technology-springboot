package com.technology.order.services;

import com.technology.activity.models.ActivityStatus;
import com.technology.activity.repositories.ActivityRepository;
import com.technology.cart.helpers.CartServiceHelper;
import com.technology.order.mappers.OrderMapper;
import com.technology.order.models.Order;
import com.technology.order.models.OrderStatus;
import com.technology.order.registration.requests.OrderRegistrationRequest;
import com.technology.order.repositories.OrderRepository;
import com.technology.role.enums.Role;
import com.technology.security.adapters.SecurityUser;
import com.technology.shift.models.Shift;
import com.technology.shift.repositories.ShiftRepository;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.user.services.NewEmployeeService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {
    private final OrderRepository orderRepository;
    private final ShiftRepository shiftRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final OrderMapper orderMapper;
    private static final Logger logger = LoggerFactory.getLogger(NewEmployeeService.class);

    //find the earliest start time of the shift of current day;
    private LocalDateTime MIN_START = LocalDateTime.MAX;
    //find the latest end time of the shift of current day;
    private LocalDateTime MAX_END = LocalDateTime.MIN;
    private Shift currentShift;

    //TODO FOR USER ACTIVITY IMPLEMENT LAZY FETCH INSTEAD OF AN EAGER ONE
    //TODO TEST CODE
    //TODO OPTIMIZE COMMUNICATION WITH DATABASE

    @Scheduled(fixedRate = 60000)
    protected void currentShiftControl() {
        LocalDateTime currentTimeDate = LocalDateTime.now();
        /*if(MAX_END==null && MIN_START==null){
            //find max end and min start from today shifts
        }*/
        //check if the date from max end and min start
        //corresponds to the closest shift
        if(currentShift==null){
            return;
        }
        if (currentTimeDate.isAfter(MAX_END) || currentTimeDate.isBefore(MIN_START)) {
            shiftRepository.findShiftClosestToCurrentTime()
                    .ifPresentOrElse(
                            shift -> currentShift = shift,
                            ()-> logger.error("NO SHIFT CLOSEST TO THE PRESENT TIME WAS FOUND")
                    );
             shiftRepository.findEndOfTheLatestShift(currentShift.getStartTime().toLocalDate())
                    .ifPresentOrElse(
                          latestEnd -> MAX_END = latestEnd,
                            ()->logger.error("NO LATEST SHIFT END WAS FOUND")
                    );
            MIN_START = currentShift.getStartTime();
            //find shift with the closest start time
            //when comparing pass the whole date-time
            //compare using also considering date-time
        } else {
            //find shift where start >= current time && end < current time
            shiftRepository.findShiftByCurrentTime()
                    .ifPresentOrElse(
                            shift -> currentShift = shift,
                            ()-> logger.error("NO CURRENT SHIFT FOUND")
                    );
        }
        if (currentTimeDate.isAfter(currentShift.getEndTime())) {
            //stream through every user and find whether there is the difference between
            // the actual points and the potential number of points
            // if there is the difference distribute unpacked the order to the
            // employee from the next shift

            //use batch update for the list of changed current activities

            String sql = "UPDATE activity\s" +
                    "SET potential = CASE\s" +
                    "WHEN potential_points > actual_points THEN actual_points\s" +
                    "ELSE potential_points\s" +
                    "END";
            //update all employees with current status present or late with the status absent
            jdbcTemplate.update(sql);
            orderRepository.findOrdersWithOrderStatus(OrderStatus.PENDING.name()).forEach(this::distributeOrder);
            currentShift = shiftRepository.findShiftClosestToCurrentTime()
                    .orElseThrow(() -> new RuntimeException(""));
            //find the next shift
            //all the unprocessed order distribute among employees from the following shift
        }
    }

    //TODO extract this method into helper class
    private User getUserFromContext() {
        SecurityUser securityUser = CartServiceHelper.getSecurityUserFromContext();
        return userRepository.findUserByEmail(securityUser.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
    @Transactional
    public void saveOrder(OrderRegistrationRequest request) {
        //save order with status pending
        //send order to be distributed using rabbit mq
        //return the order when saving the order
        //create order mapper and map request to order
        Order order = orderMapper.orderRegistrationRequestToOrder(request);
        User user = getUserFromContext();
        order.setUser(user);
        order.setCart(user.getCart());
        Order savedOrder = orderRepository.saveAndFlush(order);
        //transfer saved order
    }


    private void distributeOrderHelper(Order order) {
        //the employees must be active and have the smallest number of points
        // among active employees with current shift

        //also the role must be staff
        //maybe i should return employees when i am setting the new shift
        //and update every time i distribute order to the user
        String sql = "UPDATE activity a " +
                "SET potential_points = potential_points + ? " +
                "FROM client c" +
                "WHERE a.user_id = c.id " +
                "AND c.role = ? " +
                "AND (c.activity_status = ? OR c.activity_status = ?) " +
                "AND potential_points = (SELECT MIN(potential_points) " +
                "FROM activity WHERE user_id = id " +
                "ORDER BY id LIMIT 1)";
        jdbcTemplate.update(sql,1,order.getCart().getCartItems().size(),
                2,Role.STAFF.name(),3,ActivityStatus.PRESENT,4,ActivityStatus.LATE);

    }


    //order is delivered using rabbit ma
    //@Async
    @RabbitListener(queues = "${rabbitmq.message.queue.order}")
    public void distributeOrder(Order order) {
        if(order != null){
            distributeOrderHelper(order);
        }
        //find user with current shift,
        // status present (the user is present on the workplace)
        // and min number of the potential points
        //(this employee search is happening only between present and late employees)
        //assign the order to be processed.
        //when the employee marks current order as processed,
        //add the points from this order to the current points
    }
}
/*
List<Activity> unprocessedActivities = activityRepository.findActivitiesByShiftAAndRole(currentShift,Role.STAFF.name());
List<Activity> updatedActivities = unprocessedActivities.stream()
                    .peek(activity -> activity.setPotentialPoints(activity.getActualPoints()))
                    .toList();
            currentShift.getEmployees().stream()
                    .map(User::getUserActivity)
                    .filter(activity -> activity.getPotentialPoints() > activity.getActualPoints())
                    .map(activity -> {
                        activity.setPotentialPoints(activity.getActualPoints());
                        activityRepository.save(activity);
                        return activity.getOrders();
                    })
                    .flatMap(orders -> orders.stream().filter(
                            order -> order.getOrderStatus().equals(OrderStatus.PLACED)))
                    .forEach(this::distributeOrder);*/


/*
ORDER DISTRIBUTION
currentShift.getEmployees().stream().
                filter(employee -> employee.getRole().equals(Role.STAFF))
                .filter(employee -> employee.getUserActivity().equals(ActivityStatus.PRESENT)
                        || employee.getUserActivity().equals(ActivityStatus.LATE))
                .min(Comparator.comparing(user -> user.getUserActivity().getPotentialPoints()))
                .ifPresentOrElse(
                        user -> {
                            Activity activity = user.getUserActivity();
                            activity.getOrders().add(order);
                            activity.setPotentialPoints(activity.getPotentialPoints() +
                                    order.getCart().getCartItems().size());
                            activityRepository.save(activity);
                        },
                        () -> logger.error("USER WITH MIN POTENTIAL POINTS NOT FOUND")
                );
* */