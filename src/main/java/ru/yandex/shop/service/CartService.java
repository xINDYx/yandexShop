package ru.yandex.shop.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.yandex.shop.exception.ProductNotFoundException;
import ru.yandex.shop.model.Cart;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.repository.CartRepository;
import ru.yandex.shop.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public CartService(CartRepository cartRepository, ProductRepository productRepository) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    public List<Cart> findAll() {
        return cartRepository.findAll();
    }

    public Cart findById(Long id) {
        return cartRepository.findById(id).orElse(null);
    }

    public void deleteById(Long id) {
        cartRepository.deleteById(id);
    }

    public void clearCart(){
        cartRepository.deleteAll();
    }

    @Transactional
    public void addToCart(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product with id " + productId + " not found"));

        Cart cart = cartRepository.findByProductId(productId)
                .map(existingCart -> {
                    existingCart.setCount(product.getCount());
                    return existingCart;
                })
                .orElseGet(() -> new Cart(null, productId, product.getImgPath(), product.getTitle(), product.getDescription(), product.getCount(), product.getPrice()));

        cartRepository.save(cart);
    }

    @Transactional
    public void deleteFromCart(Long productId) {
        Optional<Cart> cart = cartRepository.findByProductId(productId);

        cart.ifPresent(cartRepository::delete);
    }

    @Transactional
    public void increaseCountByOne(Long id) {
        cartRepository.increaseCountByOne(id);
    }

    @Transactional
    public void decreaseCountByOne(Long id) {
        cartRepository.decreaseCountByOne(id);
    }
}
