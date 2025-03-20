package ru.yandex.shop.common;

import ru.yandex.shop.model.OrderItem;

public class TestOrderItemFactory {
    public static OrderItem createDefaultOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setTitle("Test Title");
        orderItem.setCount(1);
        orderItem.setPrice(100L);
        orderItem.setImgPath("Test img path");
        orderItem.setOrderId(1L);
        return orderItem;
    }
}

