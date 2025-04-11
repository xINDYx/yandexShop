package ru.yandex.showcase.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.common.TestOrderFactory;
import ru.yandex.showcase.model.Order;
import ru.yandex.showcase.service.OrderService;

import java.util.List;

@WebFluxTest(OrderController.class)
@ContextConfiguration(classes = {OrderController.class})
class OrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("Тестирование получения списка заказов")
    void testListOrders() {
        List<Order> orders = List.of(TestOrderFactory.createDefaultOrder(), TestOrderFactory.createDefaultOrder());
        long totalAmount = orders.stream().mapToLong(Order::getTotalSum).sum();

        Mockito.when(orderService.findAllOrders()).thenReturn(Flux.fromIterable(orders));

        webTestClient.get().uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Order.class)
                .hasSize(orders.size());

        Mockito.verify(orderService).findAllOrders();
    }

    @Test
    @DisplayName("Тестирование получения заказа по ID")
    void testViewOrder() {
        Long orderId = 1L;
        Order order = TestOrderFactory.createDefaultOrder();

        Mockito.when(orderService.findById(orderId)).thenReturn(Mono.just(order));

        webTestClient.get().uri("/orders/1")
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("{\"id\":null,\"totalSum\":500}");

        Mockito.verify(orderService).findById(orderId);
    }
}


