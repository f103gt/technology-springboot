package com.technology.order.controllers;

import com.technology.order.dtos.OrderDto;
import com.technology.order.models.OrderStatus;
import com.technology.order.registration.requests.OrderRegistrationRequest;
import com.technology.order.services.OrderServiceV2;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderServiceV2 orderService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/place-order")
    public ResponseEntity<String> placeOrder(HttpServletRequest request,
                                             @RequestBody OrderRegistrationRequest orderRegistrationRequest) {
        orderService.saveOrder(orderRegistrationRequest);
        return ResponseEntity.ok("Order places successfully");
    }

    @GetMapping("/staff/get-all-pending-orders")
    public ResponseEntity<List<OrderDto>> getAllPendingOrders() {
        return ResponseEntity.ok().body(orderService.getAllPendingOrders());
    }

    @PostMapping("/staff/change-order-status-packed")
    public ResponseEntity<Void> changeOrderStatusPacked(@RequestParam("orderUI") String orderIdentifier) {
        orderService.changeOrderStatus(orderIdentifier, OrderStatus.PACKED);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/staff/change-order-status-sent")
    public ResponseEntity<Void> changeOrderStatusSend(@RequestParam("orderUI") String orderIdentifier) {
        orderService.changeOrderStatus(orderIdentifier, OrderStatus.SENT);
        return ResponseEntity.ok().build();
    }
}
