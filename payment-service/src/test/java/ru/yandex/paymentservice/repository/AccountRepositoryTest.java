package ru.yandex.paymentservice.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.paymentservice.model.Account;
import ru.yandex.paymentservice.common.TestAccountFactory;
import ru.yandex.paymentservice.config.TestcontainersConfiguration;


import static org.junit.jupiter.api.Assertions.*;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
class AccountRepositoryTest extends TestcontainersConfiguration {

    @Autowired
    private AccountRepository accountRepository;

    @DisplayName("Тестирование сохранения аккаунта в репозитории")
    @Test
    void testSaveAccount() {
        Account account = accountRepository.save(TestAccountFactory.createDefaultAccount()).block();

        assertNotNull(account);
        assertNotNull(account.getId());
        assertEquals(5000.0, account.getBalance());
    }

    @DisplayName("Тестирование чтения аккаунта из репозитория")
    @Test
    void testSaveAndReadAccount() {
        Account account = accountRepository.save(TestAccountFactory.createDefaultAccount()).block();

        Account foundAccount = accountRepository.findById(account.getId()).block();

        assertNotNull(foundAccount);
        assertEquals(5000.0, foundAccount.getBalance());
    }
}
