package ru.yandex.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ui.Model;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.yandex.shop.model.Order;
import ru.yandex.shop.service.OrderService;

import java.util.List;

@Controller
@Slf4j
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public String listOrders(Model model) {
        log.info("Start orders");
        List<Order> orders = orderService.findAllOrders();
        log.info("orders = {}", orders);
        long totalAmount = orders.stream().mapToLong(Order::getTotalSum).sum();
        log.info("totalAmount = {}", totalAmount);
        model.addAttribute("orders", orders);
        model.addAttribute("totalAmount", totalAmount);

        return "/orders";
    }

    @GetMapping("/{id}")
    public String viewOrder(@PathVariable Long id, Model model) {
        log.info("Start order");
        Order order = orderService.findById(id);
        log.info("Order = {}", order);
        model.addAttribute("order", order);
        return "/order";
    }
}
