package com.technology.order.services;

import com.rabbitmq.client.Channel;
import com.technology.activity.doas.ActivityDao;
import com.technology.cart.helpers.CartServiceHelper;
import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartRepository;
import com.technology.order.brokers.OrderMessage;
import com.technology.order.brokers.OrderMessagePublisher;
import com.technology.order.controllers.SSEController;
import com.technology.order.dtos.OrderDto;
import com.technology.order.exceptions.OrderNotFoundException;
import com.technology.order.mappers.OrderMapper;
import com.technology.order.models.Order;
import com.technology.order.models.OrderStatus;
import com.technology.order.registration.requests.OrderRegistrationRequest;
import com.technology.order.repositories.OrderRepository;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import com.technology.role.enums.Role;
import com.technology.security.adapters.SecurityUser;
import com.technology.shift.models.Shift;
import com.technology.shift.repositories.ShiftRepository;
import com.technology.user.models.User;
import com.technology.user.repositories.UserRepository;
import com.technology.validation.email.services.EmailSenderService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.data.util.Pair;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Sinks;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
@RequiredArgsConstructor
public class OrderServiceV2 {
    private final OrderRepository orderRepository;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderMapper orderMapper;
    private final ActivityDao activityDao;
    private final CartRepository cartRepository;
    private final OrderMessagePublisher messagePublisher;
    private final EmailSenderService emailSenderService;
    /* private final SimpMessagingTemplate messaging;*/
    private final Sinks.Many<String> sink = Sinks.many().multicast().onBackpressureBuffer();
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceV2.class);

    private Shift currentShift;

    private User getUserFromContext() {
        SecurityUser securityUser = CartServiceHelper.getSecurityUserFromContext();
        return userRepository.findUserByEmail(securityUser.getUser().getEmail())
                .orElseThrow();
    }

    private String generateOrderIdentifier() {
        long timestamp = Instant.now().getEpochSecond();
        int randomNum = ThreadLocalRandom.current().nextInt(100000, 999999);
        return timestamp + "-" + randomNum;
    }

    @Transactional
    public void markOrderPacked(String uniqueIdentifier, String customerEmail) {
        orderRepository.findOrderByUniqueIdentifier(uniqueIdentifier)
                .ifPresent(order -> {
                    orderRepository.updateOrderStatusByOrderId(OrderStatus.PACKED,
                            order.getId());
                    emailSenderService.sendMessage(
                            customerEmail,
                            "The order was successfully packed.\n" +
                                    "Thank you for choosing our services",
                            "Order was packed");

                    //TODO CHECK IF I CAN UPDATE PRODUCTS WITHOUT USING PRODUCT REPOSITORY
                    List<Product> products = order.getCart().getCartItems().stream()
                            .map(cartItem -> {
                                Product product = cartItem.getProduct();
                                product.setQuantity(product.getQuantity() - cartItem.getQuantity());
                                return product;
                            }).toList();
                    productRepository.saveAll(products);
                    //messaging.convertAndSend();
                });
    }

    @Transactional
    public void changeOrderStatus(String orderIdentifier, OrderStatus orderStatus) {
        orderRepository.findOrderByUniqueIdentifier(orderIdentifier)
                .ifPresentOrElse(order ->
                        {
                            if (orderStatus.equals(OrderStatus.PACKED)) {
                                markOrderPacked(orderIdentifier, order.getEmail());
                                return;
                            }
                            orderRepository
                                    .updateOrderStatusByOrderId(orderStatus, order.getId());
                            emailSenderService.sendMessage(order.getEmail(),
                                    "The order was successfully sent.\n" +
                                            "Thank you for choosing our services",
                                    "Order was sent");
                        },
                        () -> {
                            throw new OrderNotFoundException("Order not found");
                        }
                );
    }

    @Transactional
    public OrderDto getOrderWithUUID(String orderUUID) {
        return orderRepository.findOrderByUniqueIdentifier(orderUUID)
                .map(orderMapper::orderToOrderDto)
                .orElseThrow(() -> new OrderNotFoundException("Order " + orderUUID + " not found"));
    }


    @Transactional
    public List<OrderDto> getAllOrdersWithStatus(OrderStatus orderStatus) {
        String employeeEmail = getUserFromContext().getEmail();
        return orderRepository.findAllOrdersByEmployeeEmailAndOrderStatus(
                        employeeEmail, orderStatus).stream()
                .map(orderMapper::orderToOrderDto)
                .toList();
    }

    //TODO POSSIBLE RETRIEVE OF ONLY ORDER UUIDS
    @Transactional
    public List<String> getAllOrdersUUIDsWithStatus(OrderStatus orderStatus) {
        String employeeEmail = getUserFromContext().getEmail();
        return orderRepository.findAllOrdersByEmployeeEmailAndOrderStatus(
                        employeeEmail, orderStatus).stream()
                .map(Order::getUniqueIdentifier)
                .toList();
    }


    private BigDecimal calculateTotalPrice(Order order) {
        return order.getCart().getCartItems().stream()
                .map(CartItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        /*return productsCost.add(
                BigDecimal.valueOf(
                        order.getDeliveryMethod().getCost()));*/
    }

    @Transactional
    public void saveOrder(OrderRegistrationRequest request) {
        /*save order with status pending
        send order to be distributed using rabbit mq
        return the order when saving the order
        create order mapper and map request to order
        TODO SEND MESSAGE TO THE CUSTOMER INFORMING THAT THE ORDER WAS PLACED*/
        Order order = orderMapper.orderRegistrationRequestToOrder(request);
        User user = getUserFromContext();
        order.setUser(user);
        Cart cart = user.getCart();
        order.setCart(cart);
        user.setCart(null);
        cart.setUser(null);
        order.setUniqueIdentifier(generateOrderIdentifier());
        order.setTotalPrice(calculateTotalPrice(order));
        Order savedOrder = orderRepository.saveAndFlush(order);
        user.getOrders().add(savedOrder);
        userRepository.save(user);
        cart.setOrder(savedOrder);
        cartRepository.save(cart);
        emailSenderService.sendMessage(
                user.getEmail(),
                formMessageBody(savedOrder),
                "Your order was successfully placed!"
        );
        messagePublisher.publishMessage(
                new OrderMessage(savedOrder.getId(),
                        ActivityDao.countItemsQuantity(savedOrder),
                        formMessageBody(savedOrder))
        );
    }

    //TODO CREATE FUNCTION TO CALCULATE FINAL PRICE
    public static String formMessageBody(Order order) {
        StringBuilder messageBody = new StringBuilder();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        messageBody.append("ORDER DETAILS:\n");
        messageBody.append("Order Number: ").append(order.getUniqueIdentifier()).append("\n");
        messageBody.append("Customer Name: ").append(order.getFirstName()).append(" ").append(order.getLastName()).append("\n");
        messageBody.append("Customer Phone Number: ").append(order.getPhoneNumber()).append("\n");
        messageBody.append("Order Date: ").append(order.getOrderDate().format(formatter)).append("\n");

        Collection<CartItem> cartItems = order.getCart().getCartItems();
        if (cartItems != null && !cartItems.isEmpty()) {
            messageBody.append("Items:\n");
            cartItems.forEach(
                    cartItem -> {
                        Product product = cartItem.getProduct();
                        messageBody.append(" - ")
                                .append(product.getProductName()).append("\n")
                                .append("\t quantity: ").append(cartItem.getQuantity()).append("\n")
                                .append("\t sku: ").append(product.getSku()).append("\n")
                                .append("\t product price: ").append(product.getPrice()).append("\n")
                                .append("\t final price: ").append(cartItem.getFinalPrice()).append("\n")
                                .append("\n");
                    }
            );
        }
        BigDecimal deliveryCosts = BigDecimal.valueOf(order.getDeliveryMethod().getCost());
        messageBody.append("Delivery Method: ").append(order.getDeliveryMethod()).append("\n");
        messageBody.append("Delivery Costs: ").append(deliveryCosts).append("\n");
        messageBody.append("Delivery Address: ").append(order.getDeliveryAddress()).append("\n");
        //TODO GET DELIVERY PRICE
        messageBody.append("Payment Method: ").append(order.getPaymentMethod()).append("\n\n");
        messageBody.append("Total Price: ").append(order.getTotalPrice().add(deliveryCosts)).append("\n");

        return messageBody.toString();
    }

    //TODO FIND ORDER TOTAL COST !!!

    /*the employees must be active and have the smallest number of points
     among active employees with current shift

    also the role must be staff
    maybe i should return employees when i am setting the new shift
    and update every time i distribute order to the user-*/
    private void distributeOrderHelper(OrderMessage order, LocalDateTime currentTime) {
        if (currentShift == null) {
            shiftRepository.findShiftByCurrentTime()
                    .ifPresentOrElse(
                            shift -> {
                                currentShift = shift;
                                Pair<String, String> employeeData = activityDao.distributeOrder(order, shift);
                                notifyEmployee(employeeData, order.getMessageBody());
                            },
                            () -> {
                                shiftRepository.findShiftClosestToCurrentTime()
                                        .ifPresentOrElse(
                                                shift -> {
                                                    currentShift = shift;
                                                    Pair<String, String> employeeData = activityDao.distributeOrder(order, shift);
                                                    notifyEmployee(employeeData, order.getMessageBody());
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
            Pair<String, String> employeeData = activityDao.distributeOrder(order, currentShift);
            notifyEmployee(employeeData, order.getMessageBody());
        } else {
            if (currentTime.isAfter(currentShift.getEndTime())) {
                shiftRepository.findShiftClosestToCurrentTime()
                        .ifPresentOrElse(
                                shift -> {
                                    activityDao.updateAllEmployeesActivity(currentShift, Role.STAFF);
                                    currentShift = shift;
                                    orderRepository.findOrdersWithOrderStatus(
                                                    OrderStatus.PENDING)
                                            .forEach(this::distributeOrder);
                                    activityDao.distributeOrder(order, shift);
                                },
                                () -> logger.error("NO SHIFT CLOSEST TO THE PRESENT TIME WAS FOUND")
                        );
            }

        }

    }

    private void notifyEmployee(Pair<String, String> employeeData, String messageBody) {
        String message = "You have been assigned with a new order to pack.";
        emailSenderService.sendMessage(
                employeeData.getFirst(),
                messageBody,
                message
        );
        try {
            SSEController.sendNotification(employeeData.getSecond(), message);
        } catch (IOException e) {
            logger.info("""
                    STACK TRACE OCCURRED WHILE SENDING NOTIFICATION VIA EMITTER IN
                    private void distributeOrderHelper(OrderMessage order, LocalDateTime currentTime
                    """);
        }
    }

    @RabbitListener(queues = "${rabbitmq.message.queue.order}")
    public void distributeOrder(OrderMessage order,
                                Channel channel,
                                @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            if (order != null) {
                LocalDateTime currentTime = LocalDateTime.now();
                distributeOrderHelper(order, currentTime);
            }
            channel.basicAck(tag, false); // send positive acknowledgement
        } catch (Exception e) {
            channel.basicNack(tag, false, false); // send negative acknowledgement
            // handle the exception
        }
    }

    private void distributeOrder(Order order) {
        if (order != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            distributeOrderHelper(
                    new OrderMessage(order.getId(),
                            ActivityDao.countItemsQuantity(order),
                            formMessageBody(order)),
                    currentTime);
        }
    }

}


 /* String generalDestination="/staff/task-notification/general";
                                                    messaging.convertAndSend(getUserFromContext().getEmail(),generalDestination);
                                                    String destination = "/staff/task-processing/user-" + employeeEmail;

                                                    messaging.convertAndSend(destination, message);*/
//order is delivered using rabbit mq
//@Async
    /*@RabbitListener(queues = "${rabbitmq.message.queue.order}")
    public void distributeOrder(OrderMessage order) {
        if (order != null) {
            LocalDateTime currentTime = LocalDateTime.now();
            distributeOrderHelper(order, currentTime);
        }
        *//*find user with current shift,
         status present (the user is present on the workplace)
         and min number of the potential points
        (this employee search is happening only between present and late employees)
        assign the order to be processed.
        when the employee marks current order as processed,
        add the points from this order to the current points*//*
    }*/
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