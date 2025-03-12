package ru.yandex.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.shop.common.TestProductFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Product;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class ProductRepositoryTest extends TestcontainersConfiguration{

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("Тестирование сохранения продукта в репозитории")
    @Test
    void testSaveProduct() {
        Product product = TestProductFactory.createDefaultProduct();

        Product savedProduct = productRepository.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals("Test Product", savedProduct.getTitle());
        assertEquals(100L, savedProduct.getPrice());
        assertEquals("http://test.com/image", savedProduct.getImgPath());
    }

    @DisplayName("Тестирование поиска несколько продуктов в репозитории")
    @Test
    void testFindAllProduct() {
        Product productOne = TestProductFactory.createDefaultProduct();
        productRepository.save(productOne);

        Product productTwo = TestProductFactory.createDefaultProduct();
        productRepository.save(productTwo);

        List<Product> listProduct =  productRepository.findAll();

        assertEquals(2, listProduct.size());
    }

    @DisplayName("Тестирование удаления продукта в репозитории")
    @Test
    void testDeleteProduct() {
        Product product = TestProductFactory.createDefaultProduct();
        Product savedProduct = productRepository.save(product);

        productRepository.deleteById(savedProduct.getId());
        Product deleteProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        assertNull(deleteProduct);
    }

    @DisplayName("Тестирование поиска продуктов по названию")
    @Test
    void testFindByTitleContaining() {
        Product productOne = TestProductFactory.createProduct("Apple", 150L, "http://test.com/apple", "Fresh Apple", 10);
        Product productTwo = TestProductFactory.createProduct("PineApple", 200L, "http://test.com/pineapple", "Sweet Pineapple", 15);
        productRepository.save(productOne);
        productRepository.save(productTwo);

        Page<Product> result = productRepository.findByTitleContaining("Apple", PageRequest.of(0, 10));
        assertFalse(result.isEmpty());
        assertEquals(2, result.getTotalElements());
    }

    @DisplayName("Тестирование увеличения счетчика продукта")
    @Test
    void testIncreaseCountByOne() {
        Product product = TestProductFactory.createDefaultProduct();
        product = productRepository.save(product);
        int initialCount = product.getCount();

        productRepository.increaseCountByOne(product.getId());
        Optional<Product> updatedProduct = productRepository.findById(product.getId());

        assertTrue(updatedProduct.isPresent());
        assertEquals(initialCount + 1, updatedProduct.get().getCount());
    }

    @DisplayName("Тестирование уменьшения счетчика продукта")
    @Test
    void testDecreaseCountByOne() {
        Product product = TestProductFactory.createDefaultProduct();
        product = productRepository.save(product);
        int initialCount = product.getCount();

        productRepository.decreaseCountByOne(product.getId());
        Optional<Product> updatedProduct = productRepository.findById(product.getId());

        assertTrue(updatedProduct.isPresent());
        assertEquals(initialCount - 1, updatedProduct.get().getCount());
    }
}
