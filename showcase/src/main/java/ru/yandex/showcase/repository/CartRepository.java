package ru.yandex.showcase.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.model.Cart;

@Repository
public interface CartRepository extends ReactiveCrudRepository<Cart, Long> {

    Mono<Cart> findByProductId(Long productId);

    @Modifying
    @Query("UPDATE cart SET count = count + 1 WHERE id = :id")
    Mono<Integer> increaseCountByOne(Long id);

    @Modifying
    @Query("UPDATE cart SET count = count - 1 WHERE id = :id")
    Mono<Integer> decreaseCountByOne(Long id);
}
