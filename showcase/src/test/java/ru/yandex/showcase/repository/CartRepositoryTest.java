package ru.yandex.showcase.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.common.TestCartFactory;
import ru.yandex.showcase.config.TestcontainersConfiguration;
import ru.yandex.showcase.model.Cart;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CartRepositoryTest extends TestcontainersConfiguration{

    @Autowired
    private CartRepository cartRepository;

    @DisplayName("Тестирование сохранения корзины в репозитории")
    @Test
    void testSaveProduct() {
        Cart cart = TestCartFactory.createDefaultCart();

        Mono<Cart> savedCartMono = cartRepository.save(cart);

        Cart savedCart = savedCartMono.block();

        assertNotNull(savedCart.getId());
        assertEquals(1L, savedCart.getProductId());
        assertEquals("Test Product", savedCart.getTitle());
        assertEquals(100L, savedCart.getPrice());
        assertEquals("http://test.com/image", savedCart.getImgPath());
    }

    @DisplayName("Тестирование поиска нескольких продуктов корзины в репозитории")
    @Test
    void testFindAllProduct() {
        Cart productOne = TestCartFactory.createDefaultCart();
        Cart productTwo = TestCartFactory.createDefaultCart();

        Mono<Void> saveAllMono = cartRepository.save(productOne)
                .then(cartRepository.save(productTwo))
                .then();

        saveAllMono.block();

        List<Cart> listProduct = cartRepository.findAll().collectList().block();

        assertNotNull(listProduct);
        assertEquals(2, listProduct.size());
    }

    @DisplayName("Тестирование удаления продукта в репозитории")
    @Test
    void testDeleteProduct() {
        Cart product = TestCartFactory.createDefaultCart();

        Cart savedProduct = cartRepository.save(product).block();
        assertNotNull(savedProduct);

        cartRepository.deleteById(savedProduct.getId()).block();

        Cart deleteProduct = cartRepository.findById(savedProduct.getId()).block();
        assertNull(deleteProduct);
    }

    @DisplayName("Тестирование увеличения счетчика продукта")
    @Test
    void testIncreaseCountByOne() {
        Cart product = TestCartFactory.createDefaultCart();

        Cart savedProduct = cartRepository.save(product).block();
        assertNotNull(savedProduct);

        int initialCount = savedProduct.getCount();

        cartRepository.increaseCountByOne(savedProduct.getId()).block();

        Cart updatedProduct = cartRepository.findById(savedProduct.getId()).block();
        assertNotNull(updatedProduct);
        assertEquals(initialCount + 1, updatedProduct.getCount());
    }

    @DisplayName("Тестирование уменьшения счетчика продукта")
    @Test
    void testDecreaseCountByOne() {
        Cart product = TestCartFactory.createDefaultCart();

        Cart savedProduct = cartRepository.save(product).block();
        assertNotNull(savedProduct);

        int initialCount = savedProduct.getCount();

        cartRepository.decreaseCountByOne(savedProduct.getId()).block();

        Cart updatedProduct = cartRepository.findById(savedProduct.getId()).block();
        assertNotNull(updatedProduct);
        assertEquals(initialCount - 1, updatedProduct.getCount());
    }
}
