package ru.yandex.shop.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.shop.common.TestOrderFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Order;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class OrderRepositoryTest extends TestcontainersConfiguration {

    @Autowired
    private OrderRepository orderRepository;

    @DisplayName("Тестирование сохранения заказа")
    @Test
    void testSaveOrder() {
        Order order = TestOrderFactory.createDefaultOrder();

        Order savedOrder = orderRepository.save(order);

        assertNotNull(savedOrder.getId());
        assertEquals(500L, savedOrder.getTotalSum());
        assertNotNull(savedOrder.getItems());
        assertEquals(2, savedOrder.getItems().size());
    }

    @DisplayName("Тестирование поиска всех заказов")
    @Test
    void testFindAllOrders() {
        Order orderOne = TestOrderFactory.createDefaultOrder();
        orderRepository.save(orderOne);

        Order orderTwo = TestOrderFactory.createDefaultOrder();
        orderRepository.save(orderTwo);

        List<Order> orders = orderRepository.findAll();

        assertEquals(2, orders.size());
    }

    @DisplayName("Тестирование удаления заказа")
    @Test
    void testDeleteOrder() {
        Order order = TestOrderFactory.createDefaultOrder();
        Order savedOrder = orderRepository.save(order);

        orderRepository.deleteById(savedOrder.getId());
        Order deletedOrder = orderRepository.findById(savedOrder.getId()).orElse(null);

        assertNull(deletedOrder);
    }
}
