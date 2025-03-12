package ru.yandex.shop.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Cart;
import ru.yandex.shop.model.Order;
import ru.yandex.shop.service.CartService;
import ru.yandex.shop.service.OrderService;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest
@AutoConfigureMockMvc
class CartControllerTest extends TestcontainersConfiguration {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CartService cartService;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("Тестирование страницы корзины")
    void testCart() throws Exception {
        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(view().name("cart"));
    }

    @Test
    @DisplayName("Тестирование действия с элементом корзины (plus)")
    void testCountActionPlus() throws Exception {
        Long cartItemId = 1L;
        String action = "plus";

        Mockito.when(cartService.findById(cartItemId)).thenReturn(new Cart());

        mockMvc.perform(post("/cart/items/{id}", cartItemId)
                        .param("action", action))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/cart/items"));

        Mockito.verify(cartService).increaseCountByOne(cartItemId);
    }

    @Test
    @DisplayName("Тестирование действия с элементом корзины (minus)")
    void testCountActionMinus() throws Exception {
        Long cartItemId = 1L;
        String action = "minus";

        Cart cartItem = new Cart();
        cartItem.setCount(1);
        Mockito.when(cartService.findById(cartItemId)).thenReturn(cartItem);

        mockMvc.perform(post("/cart/items/{id}", cartItemId)
                        .param("action", action))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/cart/items"));

        Mockito.verify(cartService).decreaseCountByOne(cartItemId);
    }

    @Test
    @DisplayName("Тестирование удаления элемента из корзины")
    void testCountActionDelete() throws Exception {
        Long cartItemId = 1L;
        String action = "delete";

        Mockito.when(cartService.findById(cartItemId)).thenReturn(new Cart());

        mockMvc.perform(post("/cart/items/{id}", cartItemId)
                        .param("action", action))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/cart/items"));

        Mockito.verify(cartService).deleteById(cartItemId);
    }

    @Test
    @DisplayName("Тестирование невалидного действия с элементом корзины")
    void testCountActionInvalid() throws Exception {
        Long cartItemId = 1L;
        String action = "invalid";

        mockMvc.perform(post("/cart/items/{id}", cartItemId)
                        .param("action", action))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/error"));
    }

    @Test
    @DisplayName("Тестирование покупки товаров (корзина не пуста)")
    void testBuyItems() throws Exception {
        List<Cart> cartItems = List.of(new Cart());
        Order order = new Order();
        order.setId(1L);

        Mockito.when(cartService.findAll()).thenReturn(cartItems);
        Mockito.when(orderService.createOrder(cartItems)).thenReturn(order);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/orders/" + order.getId() + "?order=" + order.getId()));

        Mockito.verify(cartService).findAll();
        Mockito.verify(cartService).clearCart();
        Mockito.verify(orderService).createOrder(cartItems);
    }

    @Test
    @DisplayName("Тестирование покупки товаров (корзина пуста)")
    void testBuyItemsEmptyCart() throws Exception {
        List<Cart> cartItems = List.of();

        Mockito.when(cartService.findAll()).thenReturn(cartItems);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", "/error"));

        Mockito.verify(cartService).findAll();
        Mockito.verify(orderService, Mockito.never()).createOrder(Mockito.any());
        Mockito.verify(cartService, Mockito.never()).clearCart();
    }
}

