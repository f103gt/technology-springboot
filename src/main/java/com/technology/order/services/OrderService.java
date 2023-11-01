package com.technology.order.services;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.cart.models.CartItem;
import com.technology.cart.services.CartService;
import com.technology.order.models.Order;
import com.technology.order.models.OrderStatus;
import com.technology.order.registration.requests.OrderRegistrationRequest;
import com.technology.order.repositories.OrderRepository;
import com.technology.activity.models.Activity;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.shift.models.Shift;
import com.technology.shift.repositories.ShiftRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final CartService cartService;
    private final SimpMessagingTemplate messaging;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ShiftRepository shiftRepository,
                        CartService cartService,
                        SimpMessagingTemplate messaging) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
        this.cartService = cartService;
        this.messaging = messaging;
    }

    private LocalTime currenShiftStartTime;
    private Long numberOfStaffMembersByShift;

   /* @Scheduled(cron = "0 55 * * * ?")
    @Transactional
    public void shiftCheck() {
        Optional<Shift> currentShiftStartTimeOptional = shiftRepository.findShiftByCurrentTime();
        currentShiftStartTimeOptional.ifPresentOrElse(shift -> {
                    LocalTime shiftStartTime = shift.getStartTime();
                    if (!shiftStartTime.equals(currenShiftStartTime)) {
                        currenShiftStartTime = shift.getStartTime();
                        numberOfStaffMembersByShift = userRepository
                                .findNumberOfUsersByCurrentShiftStartTime("STAFF", currenShiftStartTime);
                    }
                },
                () -> {
                    currenShiftStartTime = null;
                    numberOfStaffMembersByShift = 0L;
                });
    }

    @Transactional
    public void saveOrder(OrderRegistrationRequest request, String userEmail) {
        User user = userRepository.findUserByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        Order order = Order.builder()
                .orderDate(LocalDate.now())
                .orderStatus(OrderStatus.builder()
                        .statusName("PLACED")
                        .build())
                .user(user)
                .cart(user.getCart())
                .build();
        cartService.deleteCart(user);
        orderRepository.save(order);
    }

    *//*
     * write query to find the number of employees whose working day is the current day
     * and whose shift is overlapping with the current time
     * (current time lies with the shift start and end hours)*//*
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void distributeOrders() {
        List<User> availableUsers =
                userRepository.findAllByRoleNameAndUserShiftStartTimeAndUserActivityStatus("STAFF", currenShiftStartTime, true);
        List<Order> unprocessedOrders = orderRepository.findAllOrdersByOrderStatus("PLACED");
        if (unprocessedOrders.isEmpty()) {
            return;
        }
        if (availableUsers.isEmpty()) {
            return;
        }
        BigInteger maxPoints = availableUsers.stream()
                .map(user -> user.getUserActivity().iterator().next().getPoints())
                .max(BigInteger::compareTo).get();
        if (!maxPoints.equals(BigInteger.ZERO)) {
            availableUsers = availableUsers.stream()
                    .sorted(Comparator.comparing(user -> user.getUserActivity().iterator().next().getPoints()))
                    .collect(Collectors.toList());

            // Calculate total quantities for each order
            List<Map.Entry<Order, Integer>> orderQuantities = unprocessedOrders.stream()
                    .map(order -> new AbstractMap.SimpleEntry<>(
                            order, order.getCart().getCartItems().stream()
                            .mapToInt(CartItem::getQuantity)
                            .sum()))
                    .collect(Collectors.toList());

            // Sort orders by total quantity in descending order
            unprocessedOrders = orderQuantities.stream()
                    .sorted(Map.Entry.<Order, Integer>comparingByValue().reversed())
                    .map(Map.Entry::getKey)
                    .collect(Collectors.toList());

        }
        int iterationSize = Math.min(availableUsers.size(), unprocessedOrders.size());
        for (int index = 0; index < iterationSize; index++) {
            User user = availableUsers.get(index);
            if (userIsActive(user)) {
                Order order = unprocessedOrders.get(index);
                String destination = "/staff/task-processing/user-" + user.getId();
                String message = "You have been assigned a to process new order.";
                //TODO add new url upon following which an employee sees all the order details
                messaging.convertAndSend(destination, message);
            }
        }
        *//*availableUsers.forEach(user -> {
            String destination = "/staff/task-processing/user-" + user.getId();
            String message = "You have been assigned a to process new order.";
            //TODO add new url upon following which an employee sees all the order details
            messaging.convertAndSend(destination, message);
        });*//*
    }*/

    private boolean userIsActive(User user) {
        return user.getUserActivity().stream()
                .anyMatch(Activity::getIsAvailable);
    }
}
