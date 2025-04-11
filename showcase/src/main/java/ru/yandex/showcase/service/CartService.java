package ru.yandex.showcase.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.exception.ProductNotFoundException;
import ru.yandex.showcase.model.Cart;
import ru.yandex.showcase.repository.CartRepository;
import ru.yandex.showcase.repository.ProductRepository;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public Flux<Cart> findAll() {
        return cartRepository.findAll();
    }

    public Mono<Cart> findById(Long id) {
        return cartRepository.findById(id);
    }

    public Mono<Void> deleteById(Long id) {
        return cartRepository.deleteById(id);
    }

    public Mono<Void> clearCart() {
        return cartRepository.deleteAll();
    }

    public Mono<Void> addToCart(Long productId) {
        return productRepository.findById(productId)
                .switchIfEmpty(Mono.error(new ProductNotFoundException("Product with id " + productId + " not found")))
                .flatMap(product -> cartRepository.findByProductId(productId)
                        .defaultIfEmpty(new Cart(null, productId, product.getImgPath(), product.getTitle(), product.getDescription(), product.getCount(), product.getPrice()))
                        .map(cart -> {
                            cart.setCount(product.getCount());
                            return cart;
                        }))
                .flatMap(cartRepository::save)
                .then();
    }

    public Mono<Void> deleteFromCart(Long productId) {
        return cartRepository.findByProductId(productId)
                .flatMap(cartRepository::delete)
                .then();
    }

    public Mono<Void> increaseCountByOne(Long id) {
        return cartRepository.increaseCountByOne(id).then();
    }

    public Mono<Void> decreaseCountByOne(Long id) {
        return cartRepository.decreaseCountByOne(id).then();
    }
}
