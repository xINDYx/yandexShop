package ru.yandex.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.shop.common.TestProductFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Product;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProductRepositoryTest extends TestcontainersConfiguration {

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("Тестирование сохранения продукта в репозитории")
    @Test
    void testSaveProduct() {
        Product product = TestProductFactory.createDefaultProduct();

        Product savedProduct = productRepository.save(product).block();

        assertNotNull(savedProduct);
        assertNotNull(savedProduct.getId());
        assertEquals("Test Product", savedProduct.getTitle());
        assertEquals(100L, savedProduct.getPrice());
        assertEquals("http://test.com/image", savedProduct.getImgPath());
    }

    @DisplayName("Тестирование поиска нескольких продуктов в репозитории")
    @Test
    void testFindAllProduct() {
        Product productOne = TestProductFactory.createDefaultProduct();
        Product productTwo = TestProductFactory.createDefaultProduct();

        productRepository.save(productOne).block();
        productRepository.save(productTwo).block();

        long count = productRepository.findAll().count().block();

        assertEquals(2, count);
    }

    @DisplayName("Тестирование удаления продукта в репозитории")
    @Test
    void testDeleteProduct() {
        Product product = TestProductFactory.createDefaultProduct();
        Product savedProduct = productRepository.save(product).block();

        productRepository.deleteById(savedProduct.getId()).block();
        Product deletedProduct = productRepository.findById(savedProduct.getId()).block();

        assertNull(deletedProduct);
    }

    @DisplayName("Тестирование поиска продуктов по названию")
    @Test
    void testFindByTitleContaining() {
        Product productOne = TestProductFactory.createProduct("Apple", 150L, "http://test.com/apple", "Fresh Apple", 10);
        Product productTwo = TestProductFactory.createProduct("PineApple", 200L, "http://test.com/pineapple", "Sweet Pineapple", 15);

        productRepository.save(productOne).block();
        productRepository.save(productTwo).block();

        long count = productRepository.findByTitleContaining("Apple", 10, 0)
                .collectList()
                .map(List::size)
                .block();

        assertEquals(2, count);
    }

    @DisplayName("Тестирование увеличения счетчика продукта")
    @Test
    void testIncreaseCountByOne() {
        Product product = TestProductFactory.createDefaultProduct();
        product = productRepository.save(product).block();
        int initialCount = product.getCount();

        productRepository.increaseCountByOne(product.getId()).block();
        Product updatedProduct = productRepository.findById(product.getId()).block();

        assertNotNull(updatedProduct);
        assertEquals(initialCount + 1, updatedProduct.getCount());
    }

    @DisplayName("Тестирование уменьшения счетчика продукта")
    @Test
    void testDecreaseCountByOne() {
        Product product = TestProductFactory.createDefaultProduct();
        product = productRepository.save(product).block();
        int initialCount = product.getCount();

        productRepository.decreaseCountByOne(product.getId()).block();
        Product updatedProduct = productRepository.findById(product.getId()).block();

        assertNotNull(updatedProduct);
        assertEquals(initialCount - 1, updatedProduct.getCount());
    }
}
