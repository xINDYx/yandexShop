package ru.yandex.showcase.common;

import ru.yandex.showcase.model.Product;

public class TestProductFactory {
    public static Product createDefaultProduct() {
        Product product = new Product();
        product.setTitle("Test Product");
        product.setDescription("Test Product");
        product.setPrice(100L);
        product.setCount(5);
        product.setImgPath("http://test.com/image");
        return product;
    }

    public static Product createProduct(String title, Long price, String imgUrl, String description, int count) {
        Product product = new Product();
        product.setTitle(title);
        product.setPrice(price);
        product.setCount(count);
        product.setDescription(description);
        product.setImgPath(imgUrl);
        return product;
    }

}
