package ru.authorization.auth.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.authorization.auth.components.CustomAuthenticationManager;

@Configuration
public class AuthenticationConfig {

    @Bean
    public CustomAuthenticationManager customAuthenticationManager() {

        return new CustomAuthenticationManager();
    }
}
