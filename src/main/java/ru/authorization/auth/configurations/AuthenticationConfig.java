package ru.authorization.auth.configurations;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.authorization.auth.components.CustomAuthenticationManager;

@Slf4j
@Configuration
public class AuthenticationConfig {

    @Bean
    public CustomAuthenticationManager customAuthenticationManager() {

        return new CustomAuthenticationManager();
    }
}
