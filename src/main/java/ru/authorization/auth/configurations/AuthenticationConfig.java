package ru.authorization.auth.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import ru.authorization.auth.components.CustomAuthenticationManager;

@Configuration
public class AuthenticationConfig {

    //Да, и эту хренотень тоже пришлось прописать.. Без неё спринг ругался
    //на невозможность создать и внедрить этот бин
    //хоть тут и логики то никакой
    @Bean
    public CustomAuthenticationManager customAuthenticationManager() {
        return new CustomAuthenticationManager();
    }
}
