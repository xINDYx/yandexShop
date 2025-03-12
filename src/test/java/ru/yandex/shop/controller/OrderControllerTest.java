package ru.yandex.shop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.shop.common.TestOrderFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Order;
import ru.yandex.shop.service.OrderService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerTest extends TestcontainersConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("Тестирование отображения списка заказов")
    void testListOrders() throws Exception {
        List<Order> orders = List.of(TestOrderFactory.createDefaultOrder(), TestOrderFactory.createDefaultOrder());
        long totalAmount = orders.stream().mapToLong(Order::getTotalSum).sum();

        Mockito.when(orderService.findAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("/orders"))
                .andExpect(model().attribute("orders", orders))
                .andExpect(model().attribute("totalAmount", totalAmount));

        Mockito.verify(orderService).findAllOrders();
    }

    @Test
    @DisplayName("Тестирование отображения заказа по ID")
    void testViewOrder() throws Exception {
        Long orderId = 1L;
        Order order = TestOrderFactory.createDefaultOrder();

        Mockito.when(orderService.findById(orderId)).thenReturn(order);

        mockMvc.perform(get("/orders/{id}", orderId))
                .andExpect(status().isOk())
                .andExpect(view().name("/order"))
                .andExpect(model().attribute("order", order));

        Mockito.verify(orderService).findById(orderId);
    }
}

