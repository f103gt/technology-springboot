package com.technology.order.controllers;

import com.technology.order.registration.requests.OrderRegistrationRequest;
import com.technology.order.services.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@PreAuthorize("hasRole('USER')")
public class OrderController {
    private final OrderService orderService;

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/place-order")
    public ResponseEntity<String> placeOrder(HttpServletRequest request,
                                             @RequestBody OrderRegistrationRequest orderRegistrationRequest){
        String username = request.getRemoteUser();
        orderService.saveOrder(orderRegistrationRequest,username);
        return ResponseEntity.ok("Order places successfully");
    }
}
