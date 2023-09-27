package com.technology.cart.services;

import com.technology.cart.models.Cart;
import com.technology.cart.repositories.CartRepository;
import com.technology.cart.test.repositories.TestCartItemRepository;
import com.technology.factory.TestCartFactory;
import com.technology.factory.TestProductFactory;
import com.technology.factory.TestUserFactory;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import com.technology.user.registration.models.User;
import com.technology.user.registration.repositories.UserRepository;
import com.technology.security.adapters.SecurityUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;


@DataJpaTest
@Transactional
public class CartServiceTest {
    @Autowired
    protected CartRepository cartRepository;

    @Autowired
    protected ProductRepository productRepository;

    @Autowired
    protected TestCartItemRepository testCartItemRepository;

    @Autowired
    protected UserRepository userRepository;

    @MockBean
    protected CartService cartService;

    protected User user;

    protected Product product;

    protected Cart cart;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartServiceImpl(cartRepository,
                productRepository,
                userRepository);
    }

    @BeforeEach
    public void setUp() {
        user = TestUserFactory.createUser();
        userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(securityUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        cart = TestCartFactory.createCart(1, user);
        cartRepository.save(cart);
        userRepository.save(user);

        product = TestProductFactory.createProduct();
        productRepository.save(product);
    }
}
