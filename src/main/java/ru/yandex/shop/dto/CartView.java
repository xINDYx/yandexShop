package ru.yandex.shop.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.yandex.shop.model.Cart;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartView {

    private List<Cart> items;
    private long total;
    private boolean hasItems;
}
