package io.eclectics.cicd.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.ReactiveJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.savedrequest.NoOpServerRequestCache;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {
    @Autowired
    private ReactiveJwtDecoder jwtDecoder;

    @Bean
    @Order(1)
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) {
        return http
                .securityMatcher(new BearerTokenMatcher())
                .requestCache()
                .requestCache(NoOpServerRequestCache.getInstance())
                .and()
                .csrf().disable()
                .cors().disable()
                .formLogin().disable()
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
                .anyExchange().authenticated()
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED)))
                .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
                .and()
                .oauth2ResourceServer()
                .jwt(jwtSpec -> jwtSpec.jwtDecoder(jwtDecoder).jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                .and()
                .build();
    }

    Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new GrantedAuthoritiesExtractor());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public static class BearerTokenMatcher implements ServerWebExchangeMatcher {
        @Override
        public Mono<MatchResult> matches(ServerWebExchange exchange) {
            String auth = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

            if (auth != null && auth.startsWith("Bearer")) {
                return MatchResult.match();
            }
            return MatchResult.notMatch();
        }
    }
}
