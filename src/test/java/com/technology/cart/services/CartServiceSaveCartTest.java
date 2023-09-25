package com.technology.cart.services;

import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.cart.repositories.CartRepository;
import com.technology.cart.test.repositories.TestCartItemRepository;
import com.technology.product.repositories.ProductRepository;
import com.technology.registration.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class CartServiceSaveCartTest extends CartServiceTest{
    public CartServiceSaveCartTest(UserRepository userRepository,
                                   CartRepository cartRepository,
                                   ProductRepository productRepository,
                                   TestCartItemRepository testCartItemRepository) {
        super(cartRepository, productRepository, userRepository, testCartItemRepository);
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

}
