/*
package com.technology.order.services;

import com.technology.order.models.Order;
import com.technology.shift.models.Shift;
import com.technology.shift.repositories.ShiftRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceV2Test {
    @Mock
    private ShiftRepository shiftRepository;
    @InjectMocks
    private OrderServiceV2 orderService;
    private Order order;

    @BeforeEach
    public void setUp() {
        order = Order.builder()
                .build();
    }

    @Test
    public void testOrderDistributionHelper_SHIFT_SET_UP() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        Shift mockedShift = Shift.builder()
                .startTime(LocalDateTime.of(2023, 11, 15, 9, 0))
                .endTime(LocalDateTime.of(2023, 11, 15, 17, 0))
                .build();

        Method privateMethod = OrderServiceV2.class.getDeclaredMethod("distributeOrderHelper", Order.class,
                LocalDateTime.class);
        privateMethod.setAccessible(true);
        Field currentShiftField = OrderServiceV2.class.getDeclaredField("currentShift");
        currentShiftField.setAccessible(true);

        LocalDateTime specificTime = LocalDateTime.of(2023, 11, 15, 10, 0);
        when(shiftRepository.findShiftByCurrentTime()).thenReturn(Optional.of(mockedShift));
        privateMethod.invoke(orderService, order,specificTime);
        Shift currentShift = (Shift) currentShiftField.get(orderService);

        // Assertions
        assertThat(currentShift.getStartTime()).isEqualTo(mockedShift.getStartTime());
    }

    @Test
    public void testOrderDistributionHelper_TIME_WITHIN_CURRENT_SHIFT() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        LocalDateTime startTime = LocalDateTime.of(2023, 11, 15, 9, 0);
        LocalDateTime endTime = LocalDateTime.of(2023, 11, 15, 17, 0);
        Shift mockedShift = Shift.builder()
                .startTime(startTime)
                .endTime(endTime)
                .build();

        Method privateMethod = OrderServiceV2.class.getDeclaredMethod("distributeOrderHelper", Order.class,LocalDateTime.class);
        privateMethod.setAccessible(true);
        Field currentShiftField = OrderServiceV2.class.getDeclaredField("currentShift");
        currentShiftField.setAccessible(true);
        // Mock the behavior of currentShift
        currentShiftField.set(orderService,mockedShift);

        LocalDateTime specificTime = LocalDateTime.of(2023, 11, 15, 10, 0);

        // Call the method under test
        privateMethod.invoke(orderService, order,specificTime);

        // Assertions
        Shift currentShift = (Shift) currentShiftField.get(orderService);
        assertThat(currentShift.getStartTime()).isEqualTo(startTime);

        specificTime = LocalDateTime.of(2023, 11, 15, 9, 0);

        // Call the method under test
        privateMethod.invoke(orderService, order,specificTime);

        // Assertions
        currentShift = (Shift) currentShiftField.get(orderService);
        assertThat(currentShift.getStartTime()).isEqualTo(startTime);

    }


    @Test
    public void testOrderDistributionHelper_TIME_AFTER_CURRENT_SHIFT() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        //TESTING HOW THE SHIFT IS BEING RESET
        Shift mockedShift = Shift.builder()
                .startTime(LocalDateTime.of(2023, 11, 15, 9, 0))
                .endTime(LocalDateTime.of(2023, 11, 15, 17, 0))
                .build();

        Shift mockedShift2 = Shift.builder()
                .startTime(LocalDateTime.of(2023, 11, 15, 17, 0))
                .endTime(LocalDateTime.of(2023, 11, 15, 23, 0))
                .build();

        Method privateMethod = OrderServiceV2.class.getDeclaredMethod("distributeOrderHelper", Order.class,LocalDateTime.class);
        privateMethod.setAccessible(true);
        Field currentShiftField = OrderServiceV2.class.getDeclaredField("currentShift");
        currentShiftField.setAccessible(true);
        // Mock the behavior of currentShift
        currentShiftField.set(orderService,mockedShift);

        LocalDateTime specificTime = LocalDateTime.of(2023, 11, 15, 17, 5);

        // Assertions
        Shift currentShift = (Shift) currentShiftField.get(orderService);
        assertThat(currentShift.getStartTime()).isEqualTo(mockedShift.getStartTime());

        when(shiftRepository.findShiftClosestToCurrentTime()).thenReturn(Optional.of(mockedShift2));

        // Call the method under test
        privateMethod.invoke(orderService, order,specificTime);

        currentShift = (Shift) currentShiftField.get(orderService);

        assertThat(currentShift.getStartTime()).isEqualTo(mockedShift2.getStartTime());
    }


    @Test
    public void testOrderDistributionHelper_TIME_BEFORE_CURRENT_SHIFT_AND_SHIFT_IS_SET() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, NoSuchFieldException {
        //TESTING HOW THE SHIFT IS BEING RESET
        Shift mockedShift = Shift.builder()
                .startTime(LocalDateTime.of(2023, 11, 15, 9, 0))
                .endTime(LocalDateTime.of(2023, 11, 15, 17, 0))
                .build();

        Method privateMethod = OrderServiceV2.class.getDeclaredMethod("distributeOrderHelper", Order.class,LocalDateTime.class);
        privateMethod.setAccessible(true);
        Field currentShiftField = OrderServiceV2.class.getDeclaredField("currentShift");
        currentShiftField.setAccessible(true);
        // Mock the behavior of currentShift
        currentShiftField.set(orderService,mockedShift);

        LocalDateTime specificTime = LocalDateTime.of(2023, 11, 15, 8, 5);

        // Assertions
        Shift currentShift = (Shift) currentShiftField.get(orderService);
        assertThat(currentShift.getStartTime()).isEqualTo(mockedShift.getStartTime());

        // Call the method under test
        privateMethod.invoke(orderService, order,specificTime);

        currentShift = (Shift) currentShiftField.get(orderService);

        verify(shiftRepository, times(0)).findShiftClosestToCurrentTime();

        assertThat(currentShift.getStartTime()).isEqualTo(mockedShift.getStartTime());
    }

}
*/
