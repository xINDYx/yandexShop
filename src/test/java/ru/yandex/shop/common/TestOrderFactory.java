package ru.yandex.shop.common;

import ru.yandex.shop.model.Order;

public class TestOrderFactory {
    public static Order createDefaultOrder() {
        Order order = new Order();
        order.setTotalSum(500L);
        return order;
    }

    public static Order createOrder(Long totalSum) {
        Order order = new Order();
        order.setTotalSum(totalSum);
        return order;
    }
}

