package ru.yandex.paymentservice.service;

import ru.yandex.paymentservice.repository.AccountRepository;
import ru.yandex.paymentservice.model.BalanceDto;
import ru.yandex.paymentservice.model.PaymentRequestDto;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    private final AccountRepository accountRepository;

    public PaymentService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }


    public Mono<BalanceDto> getBalance(Long accountId) {

        return accountRepository.findById(accountId)
                .map(account -> new BalanceDto().balance(account.getBalance()))
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")));
    }

    public Mono<Void> makePayment(PaymentRequestDto request, Long accountId) {

        return accountRepository.findById(accountId)
                .switchIfEmpty(Mono.error(new RuntimeException("Account not found")))
                .flatMap(account -> {
                    Double currentAmount = account.getBalance();
                    Double paymentAmount = request.getAmount();

                    if (paymentAmount == null || paymentAmount <= 0) {
                        return Mono.error(new IllegalArgumentException("Invalid payment amount"));
                    }

                    if (currentAmount < paymentAmount.longValue()) {
                        return Mono.error(new IllegalStateException("Insufficient funds"));
                    }

                    account.setBalance(currentAmount - paymentAmount.longValue());
                    return accountRepository.save(account).then();
                });
    }
}
