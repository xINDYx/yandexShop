package ru.yandex.shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.shop.dto.CartView;
import ru.yandex.shop.service.CartService;
import ru.yandex.shop.service.OrderService;

import java.net.URI;

@Slf4j
@RestController
@RequestMapping
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    public CartController(CartService cartService, OrderService orderService) {
        this.cartService = cartService;
        this.orderService = orderService;
    }

    @GetMapping("/cart/items")
    public Mono<ResponseEntity> listProducts() {
        return cartService.findAll()
                .collectList()
                .map(cart -> {
                    boolean hasItems = !cart.isEmpty();
                    long totalPrice = cart.stream()
                            .mapToLong(item -> item.getPrice() * item.getCount())
                            .sum();

                    return ResponseEntity.ok()
                            .body(new CartView(cart, totalPrice, hasItems));
                });
    }

    @PostMapping("/cart/items/{id}")
    public Mono<ResponseEntity<Object>> countAction(
            @PathVariable("id") Long id,
            @RequestParam("action") String action) {

        return cartService.findById(id)
                .flatMap(cart -> {
                    switch (action) {
                        case "minus":
                            if (cart.getCount() > 0) {
                                cartService.decreaseCountByOne(id);
                            }
                            break;
                        case "plus":
                            cartService.increaseCountByOne(id);
                            break;
                        case "delete":
                            cartService.deleteById(id);
                            break;
                        default:
                            return Mono.error(new IllegalArgumentException("Invalid action"));
                    }
                    return Mono.just(ResponseEntity.ok().build());
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Cart item not found")));
    }

    @RequestMapping(value = "/buy", method = RequestMethod.POST)
    public Mono<ResponseEntity<Void>> buyItems(ServerWebExchange exchange) {
        return cartService.findAll()
                .collectList()
                .flatMap(cartList -> {
                    if (cartList.isEmpty()) {
                        return Mono.error(new RuntimeException("Cart is empty"));
                    }

                    return orderService.createOrder(Flux.fromIterable(cartList))
                            .flatMap(order -> {
                                cartService.clearCart();
                                String redirectUrl = "/orders/" + order.getId();
                                exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                                exchange.getResponse().getHeaders().setLocation(URI.create(redirectUrl));

                                return Mono.just(ResponseEntity.ok().build());
                            });
                });
    }
}
