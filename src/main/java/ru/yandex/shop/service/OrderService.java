package ru.yandex.shop.service;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import ru.yandex.shop.model.Cart;
import ru.yandex.shop.model.Order;
import ru.yandex.shop.model.OrderItem;
import ru.yandex.shop.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Order findById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new RuntimeException("Order not found"));
    }

    @Transactional
    public Order createOrder(List<Cart> carts) {
        Order order = new Order();

        long totalSum = carts.stream()
                .mapToLong(cart -> cart.getPrice() * cart.getCount())
                .sum();
        order.setTotalSum(totalSum);

        List<OrderItem> orderItems = carts.stream()
                .map(cart -> new OrderItem(null, cart.getTitle(), cart.getCount(), cart.getPrice(), cart.getImgPath(),order))
                .collect(Collectors.toList());

        order.setItems(orderItems);

        return orderRepository.save(order);
    }
}
