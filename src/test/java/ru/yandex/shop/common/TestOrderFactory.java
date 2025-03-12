package ru.yandex.shop.common;

import ru.yandex.shop.model.Cart;
import ru.yandex.shop.model.Order;
import ru.yandex.shop.model.OrderItem;

import java.util.Arrays;
import java.util.List;

public class TestOrderFactory {
    public static Order createDefaultOrder() {
        Order order = new Order();
        order.setTotalSum(500L);
        order.setItems(createDefaultOrderItems(order));
        return order;
    }

    public static Order createOrder(Long totalSum, List<OrderItem> items) {
        Order order = new Order();
        order.setTotalSum(totalSum);
        order.setItems(items);
        return order;
    }

    private static List<OrderItem> createDefaultOrderItems(Order order) {
        OrderItem item1 = new OrderItem();
        item1.setTitle("Test Product 1");
        item1.setCount(2);
        item1.setPrice(100L);
        item1.setOrder(order);

        OrderItem item2 = new OrderItem();
        item2.setTitle("Test Product 2");
        item2.setCount(3);
        item2.setPrice(100L);
        item2.setOrder(order);

        return Arrays.asList(item1, item2);
    }
}

