package ru.yandex.paymentservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.paymentservice.service.PaymentService;
import ru.yandex.paymentservice.api.PaymentApi;
import ru.yandex.paymentservice.model.BalanceDto;
import ru.yandex.paymentservice.model.PaymentRequestDto;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

import org.springframework.web.server.ServerWebExchange;

import javax.validation.Valid;

@RestController
@RequestMapping("/payment")
public class PaymentController implements PaymentApi {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Override
    @GetMapping
    public Mono<ResponseEntity<BalanceDto>> getBalance(
            @RequestParam("accountId") @Valid Long accountId,
            ServerWebExchange exchange
    ) {
        return paymentService.getBalance(accountId)
                .map(ResponseEntity::ok)
                .onErrorResume(e -> Mono.just(ResponseEntity.notFound().build()));
    }

    @Override
    @PostMapping
    public Mono<ResponseEntity<Void>> makePayment(
            @RequestParam("accountId") @Valid Long accountId,
            Mono<PaymentRequestDto> paymentRequestDto,
            ServerWebExchange exchange
    ) {
        return paymentRequestDto
                .flatMap(dto -> paymentService.makePayment(dto, accountId))
                .thenReturn(ResponseEntity.ok().<Void>build())
                .onErrorResume(e -> {
                    if (e instanceof IllegalArgumentException || e instanceof IllegalStateException) {
                        return Mono.just(ResponseEntity.badRequest().build());
                    }
                    return Mono.just(ResponseEntity.status(503).build());
                });
    }
}
