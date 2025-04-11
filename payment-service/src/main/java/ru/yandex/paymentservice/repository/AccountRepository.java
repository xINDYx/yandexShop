package ru.yandex.paymentservice.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.paymentservice.model.Account;

@Repository
public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
}
