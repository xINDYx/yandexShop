package ru.yandex.showcase.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import ru.yandex.showcase.payment.api.PaymentApi;
import ru.yandex.showcase.payment.invoker.ApiClient;

@Configuration
public class PaymentApiConfiguration {

    @Bean
    public PaymentApi paymentApi(WebClient.Builder webClientBuilder) {
        WebClient webClient = webClientBuilder.baseUrl("http://payment-service:8081")
                .build();

        return new PaymentApi(new ApiClient(webClient));
    }
}
