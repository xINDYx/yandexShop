package ru.yandex.showcase.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.showcase.common.TestOrderFactory;
import ru.yandex.showcase.config.TestcontainersConfiguration;
import ru.yandex.showcase.model.Order;

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

        Order savedOrder = orderRepository.save(order).block();
        assertNotNull(savedOrder);

        assertNotNull(savedOrder.getId());
        assertEquals(500L, savedOrder.getTotalSum());
    }

    @DisplayName("Тестирование поиска всех заказов")
    @Test
    void testFindAllOrders() {
        Order orderOne = TestOrderFactory.createDefaultOrder();
        Order orderTwo = TestOrderFactory.createDefaultOrder();

        orderRepository.save(orderOne)
                .then(orderRepository.save(orderTwo))
                .block();

        List<Order> orders = orderRepository.findAll().collectList().block();

        assertNotNull(orders);
        assertEquals(2, orders.size());
    }

    @DisplayName("Тестирование удаления заказа")
    @Test
    void testDeleteOrder() {
        Order order = TestOrderFactory.createDefaultOrder();

        Order savedOrder = orderRepository.save(order).block();
        assertNotNull(savedOrder);

        orderRepository.deleteById(savedOrder.getId()).block();

        Order deletedOrder = orderRepository.findById(savedOrder.getId()).block();
        assertNull(deletedOrder);
    }
}
