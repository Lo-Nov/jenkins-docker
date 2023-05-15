package io.eclectics.cicd.config;

import io.eclectics.cicd.security.BasicAuthenticationManager;
import io.eclectics.cicd.security.user.ReactiveUserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class BasicAuthSecurityConfig {
    @Value("${user.username}")
    private String adminUsername;
    @Value("${user.password}")
    private String adminPassword;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Bean
    @Order(2)
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(
                        "/",
                        "/swagger-ui/**",
                        "/webjars/**",
                        "/actuator/**",
                        "/swagger-resources/**",
                        "/v3/api-docs",
                        "/v2/api-docs",
                        "/api/v1/user/**"

                )
                .permitAll()
                .anyExchange()
                .authenticated()
                .and()
                .httpBasic()
                .and()
                .formLogin().disable()
                .build();
    }

    @Bean
    public BasicAuthenticationManager basicAuthenticationManager(ReactiveUserDetailsImpl reactiveUserDetails) {
        return new BasicAuthenticationManager(reactiveUserDetails);
    }

}
