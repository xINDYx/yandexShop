package ru.yandex.paymentservice.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public Mono<ResponseEntity<Void>> handleBadRequest(RuntimeException ex) {
        return Mono.just(ResponseEntity.badRequest().build());
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<Void>> handleOtherExceptions(Exception ex) {
        return Mono.just(ResponseEntity.status(503).build());
    }
}
