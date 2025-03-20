package ru.yandex.shop.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import ru.yandex.shop.model.Product;

@Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {

    @Query("SELECT * FROM products WHERE title ILIKE '%' || :name || '%' LIMIT :limit OFFSET :offset")
    Flux<Product> findByTitleContaining(String name, int limit, int offset);

    @Query("UPDATE products SET count = count + 1 WHERE id = :id")
    Mono<Void> increaseCountByOne(@Param("id") Long id);

    @Query("UPDATE products SET count = count - 1 WHERE id = :id")
    Mono<Void> decreaseCountByOne(@Param("id") Long id);

}
