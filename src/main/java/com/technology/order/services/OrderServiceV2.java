package com.technology.order.services;

import com.technology.activity.doas.ActivityDao;
import com.technology.cart.helpers.CartServiceHelper;
import com.technology.order.brokers.OrderMessagePublisher;
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
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {
    private final OrderRepository orderRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final OrderMapper orderMapper;
    private final ActivityDao activityDao;
    private final OrderMessagePublisher messagePublisher;
    private static final Logger logger = LoggerFactory.getLogger(NewEmployeeService.class);
    private Shift currentShift;
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
        //TODO decrease the ordered product quantity
        Order order = orderMapper.orderRegistrationRequestToOrder(request);
        User user = getUserFromContext();
        order.setUser(user);
        order.setCart(user.getCart());
        Order savedOrder = orderRepository.saveAndFlush(order);
        messagePublisher.publishMessage(savedOrder);
        //transfer saved order
    }


    //the employees must be active and have the smallest number of points
    // among active employees with current shift

    //also the role must be staff
    //maybe i should return employees when i am setting the new shift
    //and update every time i distribute order to the user
    private void distributeOrderHelper(Order order,LocalDateTime currentTime) {
        if (currentShift == null) {
            shiftRepository.findShiftByCurrentTime()
                    .ifPresentOrElse(
                            shift -> {
                                currentShift = shift;
                                activityDao.distributeOrder(order, shift);
                            },
                            () -> {
                                shiftRepository.findShiftClosestToCurrentTime()
                                        .ifPresentOrElse(
                                                shift -> {
                                                    currentShift = shift;
                                                    activityDao.distributeOrderClosestShift(order, shift);
                                                },
                                                () -> logger.error("NO SHIFT CLOSEST TO THE PRESENT TIME WAS FOUND")
                                        );
                            }
                    );

            return;
        }
        LocalDateTime shiftStart = currentShift.getStartTime();
        LocalDateTime shiftEnd = currentShift.getEndTime();
        if ((currentTime.isAfter(shiftStart) || currentTime.isEqual(shiftStart)) &&
                (currentTime.isBefore(shiftEnd))) {
            activityDao.distributeOrder(order, currentShift);
        } else {
            if(currentTime.isAfter(currentShift.getEndTime())){
                shiftRepository.findShiftClosestToCurrentTime()
                        .ifPresentOrElse(
                                shift -> {
                                    activityDao.updateAllEmployeesActivity(currentShift, Role.STAFF);
                                    currentShift = shift;
                                    orderRepository.findOrdersWithOrderStatus(
                                            OrderStatus.PENDING.name()).forEach(this::distributeOrder);
                                    activityDao.distributeOrder(order, shift);
                                },
                                () -> logger.error("NO SHIFT CLOSEST TO THE PRESENT TIME WAS FOUND")
                        );
            }

        }

    }


    //order is delivered using rabbit mq
    //@Async
    @RabbitListener(queues = "${rabbitmq.message.queue.order}")
    public void distributeOrder(Order order) {
        if (order != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            distributeOrderHelper(order,currentTime);
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