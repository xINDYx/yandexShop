package ru.yandex.showcase.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.exception.CartNotFoundException;
import ru.yandex.showcase.exception.IllegalActionException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalActionException.class)
    public Mono<ResponseEntity<String>> illegalActionException(IllegalActionException ex) {
        log.error("Произведена попытка некорректной операции{}", ex.toString());
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }

    @ExceptionHandler(CartNotFoundException.class)
    public Mono<ResponseEntity<String>> cartNotFoundException(CartNotFoundException ex) {
        log.error("Корзина не найдена{}", ex.toString());
        return Mono.just(ResponseEntity.badRequest().body(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<String>> handleUnexpectedException(Exception ex) {
        log.error("Произошла внутренняя ошибка{}", ex.toString());
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Произошла внутренняя ошибка. Пожалуйста, попробуйте позже."));
    }
}
