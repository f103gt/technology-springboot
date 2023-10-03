package com.technology.order.services;

import com.technology.cart.exceptions.UserNotFoundException;
import com.technology.cart.helpers.CartServiceHelper;
import com.technology.cart.services.CartService;
import com.technology.order.models.Order;
import com.technology.order.models.OrderStatus;
import com.technology.order.registration.requests.OrderRegistrationRequest;
import com.technology.order.repositories.OrderRepository;
import com.technology.security.adapters.SecurityUser;
import com.technology.user.registration.models.Activity;
import com.technology.user.registration.models.User;
import com.technology.user.registration.repositories.ActivityRepository;
import com.technology.user.registration.repositories.UserRepository;
import com.technology.user.shift.models.Shift;
import com.technology.user.shift.repositories.ShiftRepository;
import jakarta.transaction.Transactional;
import org.apache.catalina.security.SecurityUtil;
import org.aspectj.weaver.ast.Or;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

@Service
@EnableScheduling
public class OrderService {
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ShiftRepository shiftRepository;
    private final ActivityRepository activityRepository;
    private final CartService cartService;

    @Autowired
    public OrderService(OrderRepository orderRepository,
                        UserRepository userRepository,
                        ShiftRepository shiftRepository,
                        ActivityRepository activityRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.shiftRepository = shiftRepository;
        this.activityRepository = activityRepository;
        this.cartService = cartService;
    }

    private LocalTime currenShiftStartTime;
    private Long numberOfStaffMembersByShift;
    private User currentUser;

    private void findCurrentUser() {
        SecurityUser securityUser = CartServiceHelper.getSecurityUserFromContext();
        currentUser = userRepository.findUserByEmail(securityUser.getUsername())
                .orElseThrow(() -> new UserNotFoundException("user not found"));
    }

    @Scheduled(cron = "0 55 * * * ?")
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
    public void saveOrder(OrderRegistrationRequest request,String userEmail){
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

    /*
     * write query to find the number of employees whose working day is the current day
     * and whose shift is overlapping with the current time
     * (current time lies with the shift start and end hours)*/

    /*Optional<Order> orderOptional = orderRepository.findFirstOrOrderByOrderStatus("PLACED");
       if (orderOptional.isPresent()) {
           SecurityUser securityUser = CartServiceHelper.getSecurityUserFromContext();
           User user = userRepository.findUserByEmail(securityUser.getUsername())
                   .orElseThrow(() -> new UserNotFoundException("user not found"));
           //isActive(user)
           if(user.getActivity().equals("ACTIVE")){
               if(user.getHandledTasks() )
           }
       }*/
    @Scheduled(fixedRate = 5 * 60 * 1000)
    @Transactional
    public void checkForUnprocessedOrders() {
        Long numberOfUnprocessedOrders = orderRepository.findNumberOfUnprocessedOrders("PLACED");
        if (numberOfUnprocessedOrders == 0) {
            return;
        }
        if (numberOfUnprocessedOrders < numberOfStaffMembersByShift) {
            if (userIsActive()) {
                BigInteger maxPoints = activityRepository.findMaxPoints()
                        .orElse(maxPoints = BigInteger.ZERO);
                BigInteger minPoints = activityRepository.findMinPoints()
                        .orElse(minPoints = BigInteger.ZERO);
                if (!maxPoints.equals(BigInteger.ZERO)) {
                    BigInteger currentUserPoints = currentUser.getUserActivity().iterator().next().getPoints();
                    BigInteger pointsDifferenceMax = maxPoints.subtract(currentUserPoints);
                    if (pointsDifferenceMax.equals(BigInteger.ZERO)) {
                        return;
                    }
                    BigInteger pointsDifferenceMin = currentUserPoints.subtract(minPoints);
                    if (pointsDifferenceMax.compareTo(pointsDifferenceMin) <= 0) {
                        Optional<Order> orderOptional = orderRepository
                                .findOrderWithNumberOfItemsClosestToRequired(pointsDifferenceMax);
                        if(orderOptional.isPresent()){
                            Order order = orderOptional.get();
                            Activity currentUserActivity = currentUser.getUserActivity().iterator().next();
                            currentUserActivity.setIsAvailable(false);
                            activityRepository.save(currentUserActivity);
                            /*send user email with all order details
                            except for the card details
                            also create a popup for user
                            * */
                        }
                    }
                }
            }
        }
    }

    private boolean userIsActive() {
        return currentUser.getUserActivity().stream()
                .anyMatch(Activity::getIsAvailable);
    }
}
