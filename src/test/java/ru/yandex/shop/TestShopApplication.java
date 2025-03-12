package ru.yandex.shop;

import org.springframework.boot.SpringApplication;
import ru.yandex.shop.config.TestcontainersConfiguration;

public class TestShopApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShopApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
