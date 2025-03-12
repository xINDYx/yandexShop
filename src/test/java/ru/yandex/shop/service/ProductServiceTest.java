package ru.yandex.shop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.shop.common.TestProductFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProductServiceTest extends TestcontainersConfiguration {
    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    @DisplayName("Тестирование сохранения продукта")
    @Test
    void testSaveProduct() {
        Product product = TestProductFactory.createDefaultProduct();
        Product savedProduct = productService.save(product);

        assertNotNull(savedProduct.getId());
        assertEquals(product.getTitle(), savedProduct.getTitle());
    }

    @DisplayName("Тестирование поиска всех продуктов")
    @Test
    void testFindAllProducts() {
        productService.save(TestProductFactory.createDefaultProduct());
        productService.save(TestProductFactory.createDefaultProduct());

        List<Product> products = productService.findAll();
        assertEquals(2, products.size());
    }

    @DisplayName("Тестирование поиска продукта по ID")
    @Test
    void testFindProductById() {
        Product product = productService.save(TestProductFactory.createDefaultProduct());
        Product foundProduct = productService.findById(product.getId());

        assertNotNull(foundProduct);
        assertEquals(product.getTitle(), foundProduct.getTitle());
    }

    @DisplayName("Тестирование удаления продукта")
    @Test
    void testDeleteProduct() {
        Product product = productService.save(TestProductFactory.createDefaultProduct());
        productService.delete(product.getId());

        assertNull(productService.findById(product.getId()));
    }

    @DisplayName("Тестирование поиска продуктов с пагинацией")
    @Test
    void testFindAllWithPagination() {
        for (int i = 0; i < 5; i++) {
            productService.save(TestProductFactory.createDefaultProduct());
        }

        Pageable pageable = PageRequest.of(0, 3);
        Page<Product> page = productService.findAll(pageable);

        assertEquals(3, page.getContent().size());
        assertEquals(5, page.getTotalElements());
    }

    @DisplayName("Тестирование поиска продуктов по названию с пагинацией")
    @Test
    void testFindByTitleContaining() {
        productService.save(TestProductFactory.createProduct("Apple", 200L, "img1", "desc1", 5));
        productService.save(TestProductFactory.createProduct("Banana", 150L, "img2", "desc2", 3));
        productService.save(TestProductFactory.createProduct("Grapes", 180L, "img3", "desc3", 2));

        Pageable pageable = PageRequest.of(0, 2);
        Page<Product> page = productService.findByTitleContaining("Apple", pageable);

        assertEquals(1, page.getTotalElements());
    }

    @DisplayName("Тестирование увеличения количества продукта")
    @Test
    void testIncreaseProductCount() {
        Product product = productService.save(TestProductFactory.createDefaultProduct());
        productService.increaseCountByOne(product.getId());

        Product updatedProduct = productService.findById(product.getId());
        assertEquals(6, updatedProduct.getCount());
    }

    @DisplayName("Тестирование уменьшения количества продукта")
    @Test
    void testDecreaseProductCount() {
        Product product = productService.save(TestProductFactory.createDefaultProduct());
        productService.decreaseCountByOne(product.getId());

        Product updatedProduct = productService.findById(product.getId());
        assertEquals(4, updatedProduct.getCount());
    }
}
