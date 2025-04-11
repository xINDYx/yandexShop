package ru.yandex.showcase.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.model.Order;
import ru.yandex.showcase.service.OrderService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public Mono<ResponseEntity<List<Order>>> listOrders() {
        return orderService.findAllOrders()
                .collectList()
                .map(orders -> {
                    long totalAmount = orders.stream().mapToLong(Order::getTotalSum).sum();
                    log.info("Total Amount = {}", totalAmount);
                    return ResponseEntity.ok(orders);
                })
                .defaultIfEmpty(ResponseEntity.noContent().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Order>> viewOrder(@PathVariable Long id) {
        return orderService.findById(id)
                .map(order -> {
                    log.info("Order = {}", order);
                    return ResponseEntity.ok(order);
                })
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}

