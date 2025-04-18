package ru.yandex.showcase.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.model.User;

@Repository
public interface UserRepository extends ReactiveCrudRepository<User, Long> {
    Mono<User> findByUsername(String username);
}
