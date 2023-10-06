package com.technology.cart.services;

import com.technology.cart.models.Cart;
import com.technology.cart.models.CartItem;
import com.technology.factory.TestCartItemFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartServiceSaveCartTest extends CartServiceTest{
    @Test
    @DirtiesContext
    public void testSaveCart_AddsProductToExistingCartItem() {
        //arrange
        TestCartItemFactory.makeCartItem(product, cart);
        productRepository.save(product);
        cartRepository.save(cart);


        //act
        cartService.saveCart(product.getId());

        //assert
        makeAssertions(2, 20);
    }

    @Test
    @DirtiesContext
    public void testSaveCart_SavesCartItem_CreatesNewCartItem() {
        //arrange serUp()

        //act
        cartService.saveCart(product.getId());

        //asser
        makeAssertions(1, 10);
    }

    @Test
    @DirtiesContext
    public void testDeleteCart_DeletesCart() {
        //arrange
        CartItem cartItem = TestCartItemFactory.makeCartItem(product, cart);
        productRepository.save(product);
        cartRepository.save(cart);

        //act
        cartService.deleteCart(user);

        //assert
        Optional<CartItem> deletedCartItemOptional =
                testCartItemRepository.findCartItemById(cartItem.getId());

        assertThat(deletedCartItemOptional).isEmpty();

        Optional<Cart> userDeletedCartOptional =
                cartRepository.findCartById(cart.getId());

        assertThat(userDeletedCartOptional).isPresent();
    }

    @Test
    @DirtiesContext
    public void testSaveCart_SavesCartItem_CreatesNewCart_AndNewCartItem() {
        //arrange serUp()

        //act
        cartService.saveCart(product.getId());

        //asser
        makeAssertions(1, 10);
    }

    private void makeAssertions(int quantity, int price) {
        Optional<Cart> cartOptional = cartRepository.findCartByUserId(user.getId());
        assertTrue(cartOptional.isPresent());
        Set<CartItem> cartItems = new HashSet<>(cartOptional.get().getCartItems());
        assertEquals(1, cartItems.size());

        CartItem cartItemTest = cartItems.iterator().next();
        assertEquals(quantity, cartItemTest.getQuantity());
        assertEquals(BigDecimal.valueOf(price), cartItemTest.getFinalPrice());
    }

}
