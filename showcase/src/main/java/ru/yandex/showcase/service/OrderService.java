package ru.yandex.showcase.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.showcase.model.Cart;
import ru.yandex.showcase.model.Order;
import ru.yandex.showcase.model.OrderItem;
import ru.yandex.showcase.repository.OrderItemRepository;
import ru.yandex.showcase.repository.OrderRepository;

import java.util.List;
import java.util.stream.Collectors;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(OrderRepository orderRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public Flux<Order> findAllOrders() {
        return orderRepository.findAll();
    }

    public Mono<Order> findById(Long id) {
        return orderRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Order not found")));
    }

    @Transactional
    public Mono<Order> createOrder(Flux<Cart> carts) {
        return carts.collectList()
                .flatMap(cartList -> {
                    Order order = new Order();
                    long totalSum = cartList.stream()
                            .mapToLong(cart -> cart.getPrice() * cart.getCount())
                            .sum();
                    order.setTotalSum(totalSum);

                    return orderRepository.save(order)
                            .flatMap(savedOrder -> {
                                List<Mono<OrderItem>> updatedItems = cartList.stream()
                                        .map(cart -> {
                                            OrderItem orderItem = new OrderItem(
                                                    null, cart.getTitle(), cart.getCount(), cart.getPrice(), cart.getImgPath(), savedOrder.getId());
                                            return orderItemRepository.save(orderItem);
                                        })
                                        .collect(Collectors.toList());

                                return Mono.zip(updatedItems, (results) -> savedOrder);
                            });
                });
    }
}
