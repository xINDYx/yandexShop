package ru.yandex.shop.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.shop.common.TestProductFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Cart;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.repository.CartRepository;
import ru.yandex.shop.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CartServiceTest extends TestcontainersConfiguration {

    @Autowired
    private CartService cartService;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("Тестирование добавления товара в корзину")
    @Test
    void testAddToCart() {
        Product product = productRepository.save(TestProductFactory.createDefaultProduct());

        cartService.addToCart(product.getId());

        List<Cart> carts = cartRepository.findAll();
        assertEquals(1, carts.size());
        assertEquals(product.getId(), carts.get(0).getProductId());
    }

    @DisplayName("Тестирование поиска всех товаров в корзине")
    @Test
    void testFindAllCarts() {
        Product product1 = productRepository.save(TestProductFactory.createDefaultProduct());
        Product product2 = productRepository.save(TestProductFactory.createDefaultProduct());

        cartService.addToCart(product1.getId());
        cartService.addToCart(product2.getId());

        List<Cart> carts = cartService.findAll();
        assertEquals(2, carts.size());
    }

    @DisplayName("Тестирование удаления товара из корзины")
    @Test
    void testDeleteFromCart() {
        Product product = productRepository.save(TestProductFactory.createDefaultProduct());
        cartService.addToCart(product.getId());

        cartService.deleteFromCart(product.getId());

        assertTrue(cartRepository.findByProductId(product.getId()).isEmpty());
    }

    @DisplayName("Тестирование очистки корзины")
    @Test
    void testClearCart() {
        Product product1 = productRepository.save(TestProductFactory.createDefaultProduct());
        Product product2 = productRepository.save(TestProductFactory.createDefaultProduct());

        cartService.addToCart(product1.getId());
        cartService.addToCart(product2.getId());

        cartService.clearCart();

        assertTrue(cartRepository.findAll().isEmpty());
    }

    @DisplayName("Тестирование увеличения количества товара в корзине")
    @Test
    void testIncreaseCountByOne() {
        Product product = productRepository.save(TestProductFactory.createDefaultProduct());
        cartService.addToCart(product.getId());

        Cart cart = cartRepository.findByProductId(product.getId()).orElseThrow();
        int initialCount = cart.getCount();

        cartService.increaseCountByOne(cart.getId());

        Cart updatedCart = cartRepository.findById(cart.getId()).orElseThrow();
        assertEquals(initialCount + 1, updatedCart.getCount());
    }

    @DisplayName("Тестирование уменьшения количества товара в корзине")
    @Test
    void testDecreaseCountByOne() {
        Product product = productRepository.save(TestProductFactory.createDefaultProduct());
        cartService.addToCart(product.getId());

        Cart cart = cartRepository.findByProductId(product.getId()).orElseThrow();
        cartService.increaseCountByOne(cart.getId());
        int initialCount = cartRepository.findById(cart.getId()).orElseThrow().getCount();

        cartService.decreaseCountByOne(cart.getId());

        Cart updatedCart = cartRepository.findById(cart.getId()).orElseThrow();
        assertEquals(initialCount - 1, updatedCart.getCount());
    }
}
