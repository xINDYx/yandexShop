package ru.yandex.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.shop.common.TestCartFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Cart;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class CartRepositoryTest extends TestcontainersConfiguration{

    @Autowired
    private CartRepository cartRepository;

    @DisplayName("Тестирование сохранения корзины в репозитории")
    @Test
    void testSaveProduct() {
        Cart cart = TestCartFactory.createDefaultCart();

        Cart savedCart = cartRepository.save(cart);

        assertNotNull(savedCart.getId());
        assertEquals(1L, savedCart.getProductId());
        assertEquals("Test Product", savedCart.getTitle());
        assertEquals(100L, savedCart.getPrice());
        assertEquals("http://test.com/image", savedCart.getImgPath());
    }

    @DisplayName("Тестирование поиска несколько продуктов корзины в репозитории")
    @Test
    void testFindAllProduct() {
        Cart productOne = TestCartFactory.createDefaultCart();
        cartRepository.save(productOne);

        Cart productTwo = TestCartFactory.createDefaultCart();
        cartRepository.save(productTwo);

        List<Cart> listProduct =  cartRepository.findAll();

        assertEquals(2, listProduct.size());
    }

    @DisplayName("Тестирование удаления продукта в репозитории")
    @Test
    void testDeleteProduct() {
        Cart product = TestCartFactory.createDefaultCart();
        Cart savedProduct = cartRepository.save(product);

        cartRepository.deleteById(savedProduct.getId());
        Cart deleteProduct = cartRepository.findById(savedProduct.getId()).orElse(null);

        assertNull(deleteProduct);
    }

    @DisplayName("Тестирование увеличения счетчика продукта")
    @Test
    void testIncreaseCountByOne() {
        Cart product = TestCartFactory.createDefaultCart();
        product = cartRepository.save(product);
        int initialCount = product.getCount();

        cartRepository.increaseCountByOne(product.getId());
        Optional<Cart> updatedProduct = cartRepository.findById(product.getId());

        assertTrue(updatedProduct.isPresent());
        assertEquals(initialCount + 1, updatedProduct.get().getCount());
    }

    @DisplayName("Тестирование уменьшения счетчика продукта")
    @Test
    void testDecreaseCountByOne() {
        Cart product = TestCartFactory.createDefaultCart();
        product = cartRepository.save(product);
        int initialCount = product.getCount();

        cartRepository.decreaseCountByOne(product.getId());
        Optional<Cart> updatedProduct = cartRepository.findById(product.getId());

        assertTrue(updatedProduct.isPresent());
        assertEquals(initialCount - 1, updatedProduct.get().getCount());
    }
}
