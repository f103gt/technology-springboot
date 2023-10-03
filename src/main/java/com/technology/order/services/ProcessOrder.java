package com.technology.order.services;

import com.technology.order.models.Order;
import com.technology.user.registration.repositories.UserRepository;
import com.technology.user.shift.repositories.ShiftRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

public class ProcessOrder {
    private final ThreadPoolTaskExecutor executor;
    private final ShiftRepository shiftRepository;
    private final UserRepository userRepository;

    public ProcessOrder(ThreadPoolTaskExecutor executor, ShiftRepository shiftRepository, UserRepository userRepository) {
        this.executor = executor;
        this.shiftRepository = shiftRepository;
        this.userRepository = userRepository;
    }

    public void addOrderToQueue(Order order) {
        executor.submit(() -> processOrder(order));
    }

    @Async
    public void processOrder(Order order){

    }
}
