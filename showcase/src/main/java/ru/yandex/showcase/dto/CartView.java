package ru.yandex.showcase.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.yandex.showcase.model.Cart;

import java.util.List;

@Getter
@AllArgsConstructor
public class CartView {

    private List<Cart> items;
    private long total;
    private boolean hasItems;
}
