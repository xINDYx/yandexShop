package ru.yandex.showcase.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import ru.yandex.showcase.common.TestProductFactory;
import ru.yandex.showcase.config.TestcontainersConfiguration;
import ru.yandex.showcase.model.Product;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import reactor.core.publisher.Flux;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class ProductServiceTest extends TestcontainersConfiguration {

    @Autowired
    private ProductService productService;

    @DisplayName("Тестирование сохранения продукта")
    @Test
    void testSaveProduct() {
        Product product = TestProductFactory.createDefaultProduct();

        Mono<Product> savedProductMono = productService.save(product);

        StepVerifier.create(savedProductMono)
                .assertNext(savedProduct -> {
                    assertNotNull(savedProduct.getId());
                    assertEquals(product.getTitle(), savedProduct.getTitle());
                })
                .verifyComplete();
    }

    @DisplayName("Тестирование поиска всех продуктов")
    @Test
    void testFindAllProducts() {
        productService.save(TestProductFactory.createDefaultProduct()).block();
        productService.save(TestProductFactory.createDefaultProduct()).block();

        Flux<Product> productsFlux = productService.findAll();

        StepVerifier.create(productsFlux)
                .expectNextCount(2)
                .verifyComplete();
    }

    @DisplayName("Тестирование поиска продукта по ID")
    @Test
    void testFindProductById() {
        Product product = productService.save(TestProductFactory.createDefaultProduct()).block();
        Mono<Product> foundProductMono = productService.findById(product.getId());

        StepVerifier.create(foundProductMono)
                .assertNext(foundProduct -> {
                    assertNotNull(foundProduct);
                    assertEquals(product.getTitle(), foundProduct.getTitle());
                })
                .verifyComplete();
    }

    @DisplayName("Тестирование удаления продукта")
    @Test
    void testDeleteProduct() {
        Product product = productService.save(TestProductFactory.createDefaultProduct()).block();
        productService.delete(product.getId()).block();

        Mono<Product> foundProductMono = productService.findById(product.getId());

        StepVerifier.create(foundProductMono)
                .expectNextCount(0)
                .verifyComplete();
    }

    @DisplayName("Тестирование поиска продуктов по названию с пагинацией")
    @Test
    void testFindByTitleContaining() {
        productService.save(TestProductFactory.createProduct("Apple", 200L, "img1", "desc1", 5)).block();
        productService.save(TestProductFactory.createProduct("Banana", 150L, "img2", "desc2", 3)).block();
        productService.save(TestProductFactory.createProduct("Grapes", 180L, "img3", "desc3", 2)).block();

        Pageable pageable = PageRequest.of(0, 2);
        Flux<Product> productsFlux = productService.findByTitleContaining("Apple", pageable);

        StepVerifier.create(productsFlux.collectList())
                .assertNext(products -> {
                    assertEquals(1, products.size());
                })
                .verifyComplete();
    }

    @DisplayName("Тестирование увеличения количества продукта")
    @Test
    void testIncreaseProductCount() {
        Product product = productService.save(TestProductFactory.createDefaultProduct()).block();
        productService.increaseCountByOne(product.getId()).block();

        Mono<Product> updatedProductMono = productService.findById(product.getId());

        StepVerifier.create(updatedProductMono)
                .assertNext(updatedProduct -> {
                    assertEquals(6, updatedProduct.getCount());
                })
                .verifyComplete();
    }

    @DisplayName("Тестирование уменьшения количества продукта")
    @Test
    void testDecreaseProductCount() {
        Product product = productService.save(TestProductFactory.createDefaultProduct()).block();
        productService.decreaseCountByOne(product.getId()).block();

        Mono<Product> updatedProductMono = productService.findById(product.getId());

        StepVerifier.create(updatedProductMono)
                .assertNext(updatedProduct -> {
                    assertEquals(4, updatedProduct.getCount());
                })
                .verifyComplete();
    }
}
