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
        private final CartRepository cartRepository;
        private final ProductRepository productRepository;

        private final TestCartItemRepository testCartItemRepository;

        private final UserRepository userRepository;

        @Mock
        private CartService cartService;

        private User user;

        private Product product;

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
        public void setUp(){
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

        private CartItem makeCartItem(){
            return CartItem.builder()
                    .id(BigInteger.TEN)
                    .product(product)
                    .quantity(1)
                    .finalPrice(product.getPrice())
                    .build();
        }

        private void establishProductCartItemRelation(CartItem cartItem){
            product.getCartItems().add(cartItem);
            productRepository.save(product);
        }
        @Test
        @DirtiesContext
        public void testSaveCart_AddsProductToExistingCartItem() {
            //arrange
            Cart cart = makeCart();
            establishRelationUserCart(cart);

            CartItem cartItem = makeCartItem();
            cart.getCartItems().add(cartItem);

            establishProductCartItemRelation(cartItem);

            cartRepository.save(cart);

            //act
            cartService.saveCart(product.getId());

            //assert
            makeAssertions(2,20);
        }
        //TODO implement test for creation of new cart and new cart item
        @Test
        @DirtiesContext
        public void testSaveCart_SavesCartItem_CreatesNewCartItem(){
            //arrange serUp()
            Cart cart = makeCart();
            establishRelationUserCart(cart);

            //act
            cartService.saveCart(product.getId());

            //asser
           makeAssertions(1,10);
        }

        @Test
        @DirtiesContext
        public void testDeleteCart_DeletesCart(){
            //arrange
            //TODO carry out this piece of code into a separate method
            //TODO to avoid code duplication
            Cart cart = makeCart();
            establishRelationUserCart(cart);

            CartItem cartItem = makeCartItem();
            cart.getCartItems().add(cartItem);

            establishProductCartItemRelation(cartItem);

            cartRepository.save(cart);

            //act
            cartService.deleteCart();

            //assert
            Optional<CartItem> deletedCartItemOptional =
                    testCartItemRepository.findCartItemById(cartItem.getId());

            assertThat(deletedCartItemOptional).isEmpty();

            Optional<Cart> userDeletedCartOptional =
                    cartRepository.findCartById(cart.getId());

            /*Optional<Cart> deletedCartOptional =
                    cartRepository.findCartByUserId(user.getId());*/
            assertThat(userDeletedCartOptional).isEmpty();
        }

        @Test
        @DirtiesContext
        public void testSaveCart_SavesCartItem_CreatesNewCart_AndNewCartItem(){
            //arrange serUp()

            //act
            cartService.saveCart(product.getId());

            //asser
            makeAssertions(1,10);
        }

        @Test
        @DirtiesContext
        public void testDeleteCartItem_DeletesCartItem(){
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

        private void makeAssertions(int quantity,int price){
            Optional<Cart> cartOptional = cartRepository.findCartByUserId(user.getId());
            assertTrue(cartOptional.isPresent());
            Set<CartItem> cartItems = new HashSet<>(cartOptional.get().getCartItems());
            assertEquals(1, cartItems.size());

            CartItem cartItemTest = cartItems.iterator().next();
            assertEquals(quantity, cartItemTest.getQuantity());
            assertEquals(BigDecimal.valueOf(price), cartItemTest.getFinalPrice());
        }
        private Cart makeCart(){
            return Cart.builder()
                    .id(BigInteger.ONE)
                    .user(user)
                    .cartItems(new HashSet<>())
                    .build();
        }
        private void establishRelationUserCart(Cart cart){
            cartRepository.save(cart);

            user.setCart(cart);
            userRepository.save(user);
        }

    }
