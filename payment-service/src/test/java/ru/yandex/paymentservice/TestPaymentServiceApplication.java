package ru.yandex.paymentservice;

import org.springframework.boot.SpringApplication;
import ru.yandex.paymentservice.config.TestcontainersConfiguration;

public class TestPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.from(PaymentServiceApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
