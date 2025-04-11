package ru.yandex.showcase.common;

import ru.yandex.showcase.model.Order;

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

