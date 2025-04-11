package ru.yandex.paymentservice.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import ru.yandex.paymentservice.config.TestcontainersConfiguration;
import ru.yandex.paymentservice.model.Account;
import ru.yandex.paymentservice.model.BalanceDto;
import ru.yandex.paymentservice.model.PaymentRequestDto;

import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.paymentservice.common.TestAccountFactory;
import ru.yandex.paymentservice.repository.AccountRepository;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class PaymentControllerTest extends TestcontainersConfiguration {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private AccountRepository accountRepository;

    @Test
    @DisplayName("Контроллер: получение баланса — 200 OK")
    void testGetBalance() {
        Account account = accountRepository.save(TestAccountFactory.createDefaultAccount()).block();

        webTestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/payment")
                        .queryParam("accountId", account.getId())
                        .build())
                .exchange()
                .expectStatus().isOk()
                .expectBody(BalanceDto.class)
                .value(response -> assertThat(response.getBalance()).isEqualTo(5000.0));
    }

    @Test
    @DisplayName("Контроллер: успешная оплата — 200 OK")
    void testMakePaymentSuccess() {
        Account account = accountRepository.save(TestAccountFactory.createDefaultAccount()).block();
        PaymentRequestDto request = new PaymentRequestDto(3000.0);

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/payment")
                        .queryParam("accountId", account.getId())
                        .build())
                .bodyValue(request)
                .exchange()
                .expectStatus().isOk();

        Account foundAccount = accountRepository.findById(account.getId()).block();
        assertThat(foundAccount).isNotNull();
        assertThat(foundAccount.getBalance()).isEqualTo(2000.0);
    }

    @Test
    @DisplayName("Контроллер: некорректная сумма — 400 Bad Request")
    void testMakePaymentInvalidAmount() {
        Account account = accountRepository.save(TestAccountFactory.createDefaultAccount()).block();
        PaymentRequestDto request = new PaymentRequestDto(-100.0);

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/payment")
                        .queryParam("accountId", account.getId())
                        .build())
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("Контроллер: недостаточно средств — 400 Bad Request")
    void testMakePaymentInsufficientFunds() {
        Account account = accountRepository.save(TestAccountFactory.createDefaultAccount()).block();
        PaymentRequestDto request = new PaymentRequestDto(10_000.0);

        webTestClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path("/payment")
                        .queryParam("accountId", account.getId())
                        .build())
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }
}


