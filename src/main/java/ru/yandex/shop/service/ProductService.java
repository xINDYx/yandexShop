package ru.yandex.shop.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.repository.ProductRepository;

import java.util.List;

@Service
public class ProductService {

    public Product save(Product product) {
        return productRepository.save(product);
    }

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Product findById(Long id) {
        return productRepository.findById(id).orElse(null);
    }



    public void delete(Long id) {
        productRepository.deleteById(id);
    }

    public Page<Product> findAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> findByTitleContaining(String name, Pageable pageable) {
        return productRepository.findByTitleContaining(name, pageable);
    }

    public void increaseCountByOne(Long id) {
        productRepository.increaseCountByOne(id);
    }

    public void decreaseCountByOne(Long id) {
        productRepository.decreaseCountByOne(id);
    }
}
