package ru.yandex.shop.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.shop.common.TestCartFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Cart;
import ru.yandex.shop.model.Order;
import ru.yandex.shop.repository.CartRepository;
import ru.yandex.shop.repository.OrderRepository;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class OrderServiceTest extends TestcontainersConfiguration {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CartRepository cartRepository;

    @DisplayName("Тестирование создания заказа")
    @Test
    void testCreateOrder() {
        List<Cart> carts = Arrays.asList(
                cartRepository.save(TestCartFactory.createCart(1L, "Product A", 2L, "test.img", "test",100)),
                cartRepository.save(TestCartFactory.createCart(2L, "Product B", 1L, "test.img", "test",200))
        );

        Order order = orderService.createOrder(carts);

        assertNotNull(order.getId());
        assertEquals(400, order.getTotalSum());
        assertEquals(2, order.getItems().size());
    }

    @DisplayName("Тестирование поиска всех заказов")
    @Test
    void testFindAllOrders() {
        List<Cart> carts1 = List.of(cartRepository.save(TestCartFactory.createCart(1L, "Product A", 2L, "test.img", "test",100)));
        List<Cart> carts2 = List.of(cartRepository.save(TestCartFactory.createCart(2L, "Product B", 1L, "test.img", "test",200)));

        orderService.createOrder(carts1);
        orderService.createOrder(carts2);

        List<Order> orders = orderService.findAllOrders();
        assertEquals(2, orders.size());
    }

    @DisplayName("Тестирование поиска заказа по ID")
    @Test
    void testFindOrderById() {
        List<Cart> carts = List.of(cartRepository.save(TestCartFactory.createCart(1L, "Product A", 2L, "test.img", "test",100)));
        Order createdOrder = orderService.createOrder(carts);

        Order foundOrder = orderService.findById(createdOrder.getId());

        assertEquals(createdOrder.getId(), foundOrder.getId());
        assertEquals(200, foundOrder.getTotalSum());
    }
}
