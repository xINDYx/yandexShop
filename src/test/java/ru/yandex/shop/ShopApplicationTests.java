package ru.yandex.shop;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.context.ImportTestcontainers;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.yandex.shop.config.TestcontainersConfiguration;

@SpringBootTest
@Testcontainers
@ImportTestcontainers(TestcontainersConfiguration.class)
@ActiveProfiles("test")
public class ShopApplicationTests {

	@Test
	void contextLoads() {
	}
}
