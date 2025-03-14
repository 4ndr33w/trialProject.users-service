package ru.authorization.auth.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.authorization.auth.components.CustomAuthenticationManager;
import ru.authorization.auth.utils.exceptions.global.GlobalExceptionHandler;

@Configuration
public class AuthenticationConfig {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @Bean
    public CustomAuthenticationManager customAuthenticationManager() {

        log.info("Configuring custom authentication manager");
        return new CustomAuthenticationManager();
    }
}
