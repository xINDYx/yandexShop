package ru.yandex.shop.service;

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
        Product product = productRepository.save(TestProductFactory.createDefaultProduct()).block();

        cartService.addToCart(product.getId()).block();

        long cartCount = cartRepository.count().block();
        Cart cart = cartRepository.findByProductId(product.getId()).block();

        assertEquals(1, cartCount);
        assertEquals(product.getId(), cart.getProductId());
    }

    @DisplayName("Тестирование поиска всех товаров в корзине")
    @Test
    void testFindAllCarts() {
        Product product1 = productRepository.save(TestProductFactory.createDefaultProduct()).block();
        Product product2 = productRepository.save(TestProductFactory.createDefaultProduct()).block();

        cartService.addToCart(product1.getId()).block();
        cartService.addToCart(product2.getId()).block();

        long cartCount = cartRepository.count().block();

        assertEquals(2, cartCount);
    }

    @DisplayName("Тестирование удаления товара из корзины")
    @Test
    void testDeleteFromCart() {
        Product product = productRepository.save(TestProductFactory.createDefaultProduct()).block();
        cartService.addToCart(product.getId()).block();

        cartService.deleteFromCart(product.getId()).block();

        boolean isProductInCart = cartRepository.findByProductId(product.getId()).block() == null;
        assertTrue(isProductInCart);
    }

    @DisplayName("Тестирование очистки корзины")
    @Test
    void testClearCart() {
        Product product1 = productRepository.save(TestProductFactory.createDefaultProduct()).block();
        Product product2 = productRepository.save(TestProductFactory.createDefaultProduct()).block();

        cartService.addToCart(product1.getId()).block();
        cartService.addToCart(product2.getId()).block();

        cartService.clearCart().block();

        long cartCount = cartRepository.count().block();
        assertEquals(0, cartCount);
    }

    @DisplayName("Тестирование увеличения количества товара в корзине")
    @Test
    void testIncreaseCountByOne() {
        Product product = productRepository.save(TestProductFactory.createDefaultProduct()).block();
        cartService.addToCart(product.getId()).block();

        Cart cart = cartRepository.findByProductId(product.getId()).block();
        int initialCount = cart.getCount();

        cartService.increaseCountByOne(cart.getId()).block();

        Cart updatedCart = cartRepository.findById(cart.getId()).block();
        assertEquals(initialCount + 1, updatedCart.getCount());
    }

    @DisplayName("Тестирование уменьшения количества товара в корзине")
    @Test
    void testDecreaseCountByOne() {
        Product product = productRepository.save(TestProductFactory.createDefaultProduct()).block();
        cartService.addToCart(product.getId()).block();

        Cart cart = cartRepository.findByProductId(product.getId()).block();
        cartService.increaseCountByOne(cart.getId()).block();
        int initialCount = cartRepository.findById(cart.getId()).block().getCount();

        cartService.decreaseCountByOne(cart.getId()).block();

        Cart updatedCart = cartRepository.findById(cart.getId()).block();
        assertEquals(initialCount - 1, updatedCart.getCount());
    }
}
