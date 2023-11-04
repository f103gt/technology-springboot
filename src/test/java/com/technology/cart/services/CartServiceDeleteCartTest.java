/*
package com.technology.cart.services;

import com.technology.cart.models.CartItem;
import com.technology.factory.TestCartItemFactory;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
*/

/*
public class CartServiceDeleteCartTest extends CartServiceTest{
    @Test
    @DirtiesContext
    public void testDeleteCartItem_DeletesCartItem() {
        //arrange
        CartItem cartItem = TestCartItemFactory.makeCartItem(product,cart);
        productRepository.save(product);
        cartRepository.save(cart);

        cartRepository.save(cart);

        //act

        cartService.deleteCartItem(product.getId());

        //assert

            */
/*checking if the cart item was deleted successfully
            using cart item id to make the code less complex,
             however in real life we are deleting cart item
             by searching in the set of cart items in the cart
             to prevent any data inconsistency*//*


        Optional<CartItem> deletedCartItemOptional =
                testCartItemRepository.findCartItemById(cartItem.getId());
        assertThat(deletedCartItemOptional).isEmpty();

    }
}
*/
