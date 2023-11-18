package com.technology.order.brokers;

import com.technology.order.models.Order;
import com.technology.order.services.OrderServiceV2;
import lombok.RequiredArgsConstructor;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

@RunWith(MockitoJUnitRunner.class)
@SpringBootTest
@RequiredArgsConstructor
public class OrderBrokerTest {
    /*@Autowired
    private final OrderMessagePublisher messagePublisher;
    @Autowired
    private final OrderServiceV2 orderService;

    public void saveOrderAndDistributeOrderCommunicationTest() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Order order = Order.builder().build();

        Method saveOrder = OrderServiceV2.class.getDeclaredMethod("saveOrder", Order.class);
        saveOrder.setAccessible(true);

        saveOrder.invoke(orderService, order);

    }*/
}
