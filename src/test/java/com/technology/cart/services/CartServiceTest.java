    package com.technology.cart.services;

    import com.technology.cart.models.Cart;
    import com.technology.cart.models.CartItem;
    import com.technology.cart.repositories.CartRepository;
    import com.technology.product.models.Product;
    import com.technology.product.repositories.ProductRepository;
    import com.technology.registration.models.User;
    import com.technology.registration.repositories.UserRepository;
    import jakarta.transaction.Transactional;
    import org.junit.jupiter.api.BeforeAll;
    import org.junit.jupiter.api.BeforeEach;
    import org.junit.jupiter.api.Test;
    import org.mockito.Mock;
    import org.mockito.MockitoAnnotations;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
    import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
    import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
    import org.springframework.security.core.Authentication;
    import org.springframework.security.core.context.SecurityContextHolder;

    import java.math.BigDecimal;
    import java.math.BigInteger;
    import java.util.HashSet;
    import java.util.Optional;
    import java.util.Set;

    import static org.assertj.core.api.Assertions.assertThat;
    import static org.junit.jupiter.api.Assertions.assertEquals;
    import static org.junit.jupiter.api.Assertions.assertTrue;


    @DataJpaTest
    @Transactional
    public class CartServiceTest {
        private final CartRepository cartRepository;
        private final ProductRepository productRepository;

        private final UserRepository userRepository;

        @Mock
        private CartService cartService;

        private User user;

        private Product product;

        @Autowired
        public CartServiceTest(CartRepository cartRepository,
                               ProductRepository productRepository,
                               UserRepository userRepository) {
            this.cartRepository = cartRepository;
            this.productRepository = productRepository;
            this.userRepository = userRepository;
        }


        @BeforeEach
        public void init() {
            MockitoAnnotations.openMocks(this);
            cartService = new CartService(cartRepository, productRepository);
        }

        @BeforeEach
        public void setUp(){
            user = User.builder()
                    .id(BigInteger.ONE)
                    .build();
            userRepository.save(user);

            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            product = Product.builder()
                    .id(BigInteger.ONE)
                    .price(BigDecimal.TEN)
                    .cartItems(new HashSet<>())
                    .build();
            productRepository.save(product);
        }

        @Test
        public void testSaveCart_AddsProductToExistingCartItem() {
            //arrange
            Cart cart = Cart.builder()
                    .id(BigInteger.ONE)
                    .user(user)
                    .cartItems(new HashSet<>())
                    .build();
            cartRepository.save(cart);

            user.setCart(cart);
            userRepository.save(user);


            CartItem cartItem = CartItem.builder()
                    .id(BigInteger.TEN)
                    .product(product)
                    .quantity(1)
                    .finalPrice(product.getPrice())
                    .build();
            cart.getCartItems().add(cartItem);

            product.getCartItems().add(cartItem);
            productRepository.save(product);

            cartRepository.save(cart);

            //act
            cartService.saveCart(product.getId());

            //assert
            Optional<Cart> cartOptional = cartRepository.findCartByUserId(user.getId());
            /*assertThat(cartOptional).isPresent();*/
            assertTrue(cartOptional.isPresent());
            Set<CartItem> cartItems = new HashSet<>(cartOptional.get().getCartItems());
            assertEquals(1, cartItems.size());

            CartItem cartItemTest = cartItems.iterator().next();
            assertEquals(2, cartItemTest.getQuantity());
            assertEquals(BigDecimal.valueOf(20), cartItemTest.getFinalPrice());
        }

    }
