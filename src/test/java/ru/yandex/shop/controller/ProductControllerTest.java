package ru.yandex.shop.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.shop.common.TestProductFactory;
import ru.yandex.shop.config.TestcontainersConfiguration;
import ru.yandex.shop.model.Product;
import ru.yandex.shop.service.CartService;
import ru.yandex.shop.service.ProductService;

import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest extends TestcontainersConfiguration {

    @Autowired
    private MockMvc mockMvc;

    private ProductService productService;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        productService = Mockito.mock(ProductService.class);
        cartService = Mockito.mock(CartService.class);

    }

    @Test
    @DisplayName("Тестирование главной страницы продуктов без поиска")
    void testListProducts_WithoutSearch() throws Exception {
        mockMvc.perform(get("/main")
                        .param("sort", "NO")
                        .param("pageSize", "5")
                        .param("pageNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"));
    }

    @Test
    @DisplayName("Тестирование главной страницы продуктов с поиском")
    void testListProducts_WithSearch() throws Exception {
        Page<Product> productPage = new PageImpl<>(List.of(TestProductFactory.createDefaultProduct()));
        Mockito.when(productService.findByTitleContaining(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(productPage);

        mockMvc.perform(get("/main")
                        .param("search", "Test")
                        .param("sort", "NO")
                        .param("pageSize", "5")
                        .param("pageNumber", "0"))
                .andExpect(status().isOk())
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("items", "paging", "search", "sort", "pageSize"));
    }

    @Test
    @DisplayName("Тестирование открытия несуществующего продукта")
    void testGetProduct_NotExists() throws Exception {
        Mockito.when(productService.findById(1L)).thenReturn(null);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/error"));
    }

    @Test
    @DisplayName("Тестирование увеличение количества продуктов на 1")
    void testCountAction_Plus() throws Exception {
        Mockito.doNothing().when(productService).increaseCountByOne(1L);

        mockMvc.perform(post("/main/items/1/plus")
                        .param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/main?*"));
    }

    @Test
    @DisplayName("Тестирование удаления из корзины")
    void testCountAction_DeleteFromCart() throws Exception {
        Mockito.doNothing().when(cartService).deleteFromCart(1L);

        mockMvc.perform(post("/main/items/1/plus")
                        .param("action", "deleteFromCart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/main?*"));
    }

    @Test
    @DisplayName("Тестирование увеличение количества продуктов на 1 в корзине")
    void testHandleCartActions_Plus() throws Exception {
        Mockito.doNothing().when(productService).increaseCountByOne(1L);

        mockMvc.perform(post("/items/1")
                        .param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));
    }

    @Test
    @DisplayName("Тестирование уменьшения количества продуктов на 1")
    void testHandleCartActions_Minus() throws Exception {
        Mockito.doNothing().when(productService).decreaseCountByOne(1L);

        mockMvc.perform(post("/items/1")
                        .param("action", "minus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));
    }

    @Test
    @DisplayName("Тестирование удаления из корзины в корзине")
    void testHandleCartActions_DeleteFromCart() throws Exception {
        Mockito.doNothing().when(cartService).deleteFromCart(1L);

        mockMvc.perform(post("/items/1")
                        .param("action", "deleteFromCart"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/1"));
    }
}
