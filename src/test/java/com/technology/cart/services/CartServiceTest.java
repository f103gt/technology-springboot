package com.technology.cart.services;

import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartRepository;
import com.technology.cart.test.repositories.TestCartItemRepository;
import com.technology.product.models.Product;
import com.technology.product.repositories.ProductRepository;
import com.technology.registration.models.User;
import com.technology.registration.repositories.UserRepository;
import com.technology.security.adapters.SecurityUser;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.DirtiesContext;

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
    protected final CartRepository cartRepository;
    protected final ProductRepository productRepository;

    protected final TestCartItemRepository testCartItemRepository;

    protected final UserRepository userRepository;

    @Mock
    protected CartService cartService;

    protected User user;

    protected Product product;

    @Autowired
    public CartServiceTest(CartRepository cartRepository,
                           ProductRepository productRepository,
                           UserRepository userRepository,
                           TestCartItemRepository testCartItemRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.testCartItemRepository = testCartItemRepository;
    }


    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartService(cartRepository,
                productRepository,
                userRepository);
    }

    @BeforeEach
    public void setUp() {
        user = User.builder()
                .id(BigInteger.ONE)
                .firstName("Test")
                .lastName("User")
                .email("testuser@example.com")
                .password("password")
                .isEnabled(true)
                .roles(new HashSet<>())
                .addresses(new HashSet<>())
                .build();
        userRepository.save(user);

        SecurityUser securityUser = new SecurityUser(user);

        Authentication authentication = new UsernamePasswordAuthenticationToken(securityUser, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        product = Product.builder()
                .id(BigInteger.ONE)
                .price(BigDecimal.TEN)
                .cartItems(new HashSet<>())
                .build();
        productRepository.save(product);
    }

    @Test
    @DirtiesContext
    public void testDeleteCartItem_DeletesCartItem() {
        //arrange
        Cart cart = makeCart();
        establishRelationUserCart(cart);

        CartItem cartItem = makeCartItem();
        cart.getCartItems().add(cartItem);

        establishProductCartItemRelation(cartItem);

        cartRepository.save(cart);

        //act

        cartService.deleteCartItem(product.getId());

        //assert

            /*checking if the cart item was deleted successfully
            using cart item id to make the code less complex,
             however in real life we are deleting cart item
             by searching in the set of cart items in the cart
             to prevent any data inconsistency*/

        Optional<CartItem> deletedCartItemOptional =
                testCartItemRepository.findCartItemById(cartItem.getId());
        assertThat(deletedCartItemOptional).isEmpty();

    }

    protected void makeAssertions(int quantity, int price) {
        Optional<Cart> cartOptional = cartRepository.findCartByUserId(user.getId());
        assertTrue(cartOptional.isPresent());
        Set<CartItem> cartItems = new HashSet<>(cartOptional.get().getCartItems());
        assertEquals(1, cartItems.size());

        CartItem cartItemTest = cartItems.iterator().next();
        assertEquals(quantity, cartItemTest.getQuantity());
        assertEquals(BigDecimal.valueOf(price), cartItemTest.getFinalPrice());
    }

    protected Cart makeCart() {
        return Cart.builder()
                .id(BigInteger.ONE)
                .user(user)
                .cartItems(new HashSet<>())
                .build();
    }

    protected void establishRelationUserCart(Cart cart) {
        cartRepository.save(cart);

        user.setCart(cart);
        userRepository.save(user);
    }

    protected CartItem makeCartItem() {
        return CartItem.builder()
                .id(BigInteger.TEN)
                .product(product)
                .quantity(1)
                .finalPrice(product.getPrice())
                .build();
    }

    protected void establishProductCartItemRelation(CartItem cartItem) {
        product.getCartItems().add(cartItem);
        productRepository.save(product);
    }

}
