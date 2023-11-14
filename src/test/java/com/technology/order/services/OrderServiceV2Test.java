package com.technology.order.services;

import com.technology.activity.repositories.ActivityRepositoryTest;
import com.technology.order.mappers.OrderMapper;
import com.technology.order.repositories.OrderRepository;
import com.technology.shift.repositories.ShiftRepository;
import com.technology.user.repositories.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

@DataJpaTest
@Transactional
class OrderServiceV2Test {
    private final OrderRepository orderRepository;
    private final ShiftRepository shiftRepository;
    private final ActivityRepositoryTest activityRepository;
    private final UserRepository userRepository;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final OrderMapper orderMapper;

    @Autowired
    public OrderServiceV2Test(OrderRepository orderRepository,
                              ShiftRepository shiftRepository,
                              ActivityRepositoryTest activityRepository,
                              UserRepository userRepository,
                              JdbcTemplate jdbcTemplate,
                              NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                              OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.shiftRepository = shiftRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.orderMapper = orderMapper;
    }
}