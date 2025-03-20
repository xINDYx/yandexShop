package ru.yandex.shop.service;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.repository.ProductRepository;

@Service
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Mono<Product> save(Product product) {
        return productRepository.save(product);
    }

    public Flux<Product> findAll() {
        return productRepository.findAll();
    }

    public Mono<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Mono<Void> delete(Long id) {
        return productRepository.deleteById(id);
    }

    public Flux<Product> findByTitleContaining(String name, Pageable pageable) {
        int limit = pageable.getPageSize();
        int offset = (int) pageable.getOffset();
        return productRepository.findByTitleContaining(name, limit, offset);
    }

    public Mono<Void> increaseCountByOne(Long id) {
        return productRepository.increaseCountByOne(id).then();
    }

    public Mono<Void> decreaseCountByOne(Long id) {
        return productRepository.decreaseCountByOne(id).then();
    }
}
