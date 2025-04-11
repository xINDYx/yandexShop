package ru.yandex.showcase;

import org.springframework.boot.SpringApplication;
import ru.yandex.showcase.config.TestcontainersConfiguration;

public class TestShopApplication {

	public static void main(String[] args) {
		SpringApplication.from(ShopApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
