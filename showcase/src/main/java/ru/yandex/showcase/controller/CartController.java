package ru.yandex.showcase.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;
import ru.yandex.showcase.dto.CartView;
import ru.yandex.showcase.enums.CartAction;
import ru.yandex.showcase.exception.CartNotFoundException;
import ru.yandex.showcase.exception.IllegalActionException;
import ru.yandex.showcase.model.Cart;
import ru.yandex.showcase.model.Order;
import ru.yandex.showcase.payment.api.PaymentApi;
import ru.yandex.showcase.payment.model.PaymentRequestDto;
import ru.yandex.showcase.service.CartService;
import ru.yandex.showcase.service.OrderService;

import java.net.URI;
import java.util.List;

@Slf4j
@RestController
@RequestMapping
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;
    private final PaymentApi paymentApi;

    public CartController(CartService cartService, OrderService orderService, PaymentApi paymentApi) {
        this.cartService = cartService;
        this.orderService = orderService;
        this.paymentApi = paymentApi;
    }

    @GetMapping("/cart/items")
    @PreAuthorize("isAuthenticated()")
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
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<Object>> countAction(
            @PathVariable("id") Long id,
            @RequestParam("action") String actionParam) {

        CartAction action;
        try {
            action = CartAction.fromString(actionParam);
        } catch (IllegalArgumentException e) {
            return Mono.error(new IllegalActionException("Invalid action: " + actionParam));
        }

        return cartService.findById(id)
                .flatMap(cart -> {
                    Mono<Void> operation;
                    switch (action) {
                        case MINUS -> {
                            if (cart.getCount() > 0) {
                                operation = cartService.decreaseCountByOne(id);
                            } else {
                                operation = Mono.empty();
                            }
                        }
                        case PLUS -> operation = cartService.increaseCountByOne(id);
                        case DELETE -> operation = cartService.deleteById(id);
                        default -> {
                            return Mono.error(new IllegalActionException("Invalid action"));
                        }
                    }
                    return operation.then(Mono.just(ResponseEntity.ok().build()));
                })
                .switchIfEmpty(Mono.error(new CartNotFoundException("Cart item not found")));
    }

    @PostMapping("/buy")
    @PreAuthorize("isAuthenticated()")
    public Mono<ResponseEntity<Void>> buyItems(ServerWebExchange exchange) {
        Long accountId = 1L;

        return cartService.findAll()
                .collectList()
                .flatMap(cartList -> processCartAndCreateOrder(cartList, accountId, exchange));
    }

    private Mono<ResponseEntity<Void>> processCartAndCreateOrder(List<Cart> cartList, Long accountId, ServerWebExchange exchange) {
        if (cartList.isEmpty()) {
            return Mono.error(new RuntimeException("Cart is empty"));
        }

        return orderService.createOrder(Flux.fromIterable(cartList))
                .flatMap(order -> processPayment(accountId, order, exchange));
    }

    private Mono<ResponseEntity<Void>> processPayment(Long accountId, Order order, ServerWebExchange exchange) {
        double totalAmount = order.getTotalSum().doubleValue();

        return paymentApi.getBalance(accountId)
                .doOnNext(balanceDto -> log.info("Баланс = {}", balanceDto.getBalance()))
                .flatMap(balanceDto -> {
                    if (balanceDto.getBalance() < totalAmount) {
                        return Mono.error(new RuntimeException("Недостаточно средств"));
                    }

                    PaymentRequestDto paymentRequest = new PaymentRequestDto().amount(totalAmount);

                    return paymentApi.makePayment(accountId, paymentRequest)
                            .then(cartService.clearCart())
                            .then(Mono.defer(() -> {
                                String redirectUrl = "/orders/" + order.getId();
                                exchange.getResponse().setStatusCode(HttpStatus.SEE_OTHER);
                                exchange.getResponse().getHeaders().setLocation(URI.create(redirectUrl));
                                return Mono.just(ResponseEntity.ok().build());
                            }));
                });
    }

}
