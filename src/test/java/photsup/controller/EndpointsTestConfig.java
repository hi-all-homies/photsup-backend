package photsup.controller;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import photsup.service.jwt.TokenProviderHmacSha;

@TestConfiguration
@Import(TokenProviderHmacSha.class)
public class EndpointsTestConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf().disable()
                .authorizeHttpRequests().anyRequest().permitAll()
                .and()
                .build();
    }
}
