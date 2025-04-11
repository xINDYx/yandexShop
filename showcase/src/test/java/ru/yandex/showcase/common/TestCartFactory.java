package ru.yandex.showcase.common;

import ru.yandex.showcase.model.Cart;

public class TestCartFactory {
    public static Cart createDefaultCart() {
        Cart cart = new Cart();
        cart.setProductId(1L);
        cart.setTitle("Test Product");
        cart.setDescription("Test Product");
        cart.setPrice(100L);
        cart.setCount(5);
        cart.setImgPath("http://test.com/image");
        return cart;
    }

    public static Cart createCart(Long productId, String title, Long price, String imgUrl, String description, int count) {
        Cart cart = new Cart();
        cart.setProductId(productId);
        cart.setTitle(title);
        cart.setPrice(price);
        cart.setCount(count);
        cart.setDescription(description);
        cart.setImgPath(imgUrl);
        return cart;
    }
}
