package ru.yandex.paymentservice.common;

import ru.yandex.paymentservice.model.Account;

public class TestAccountFactory {
    public static Account createDefaultAccount() {
        Account account = new Account();
        account.setBalance(5000.0);
        return account;
    }

    public static Account createAccount(Double balance) {
        Account account = new Account();
        account.setBalance(balance);
        return account;
    }
}
