package ru.yandex.showcase.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;
import ru.yandex.showcase.service.UserDetailsService;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/", "/products", "/product/**", "/css/**", "/js/**").permitAll()
                        .pathMatchers("/cart/**", "/orders/**").authenticated()
                        .anyExchange().permitAll()
                )
                .formLogin(Customizer.withDefaults())
                .logout(logoutSpec -> logoutSpec
                        .logoutUrl("/logout")
                        .logoutSuccessHandler((exchange, authentication) ->
                                Mono.fromRunnable(() ->
                                                exchange.getExchange().getResponse().setStatusCode(HttpStatus.FOUND))
                                        .then(exchange.getExchange().getResponse().setComplete())
                        )
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
