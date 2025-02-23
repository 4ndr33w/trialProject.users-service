package ru.authorization.auth.configurations;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

import ru.authorization.auth.components.CustomAuthenticationManager;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.models.enums.UserStatus;
import ru.authorization.auth.repositories.TokenRepository;
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.services.CustomUserDetailsService;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private CustomUserDetailsService customUserDetailsService;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationManager customAuthenticationManager) throws Exception {

        String adminRole = UserStatus.ADMIN.toString();
        String userRole = UserStatus.USER.toString();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        //Здесь задаются условия фильтрации Ендпойнтов
                        //какие операции методы доступа по каким маршрутом дозволены без авторизации
                        //  '/*' - регламентирует один уровень вложенности: /users
                        // '/**' - регламентирует все уровни вложенности: /users/delete/{id}
                        //Если подключим зависимость springframework.security
                        //и не настроим цепочку фильтрации,
                        //то все запросы будут возвращать 401
                        //Если подключим зависимость springframework.security.web
                        //и не настроим цепочку фильтрации,
                        //то все запросы будут возвращать 403
                        .requestMatchers(HttpMethod.GET,"/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        .requestMatchers(HttpMethod.DELETE,"/**").permitAll()
                        .requestMatchers(HttpMethod.PUT,"/**").permitAll()
                        .anyRequest().authenticated()
                )

                //httpBasic - тип аутенфикации. В данном случае у нас Basic Auth
                .httpBasic(withDefaults())
                .addFilter(new AuthenticationFilter(

                        //Далее у нас идёт CustomAuthenticationManager -
                        //Если мы его не переопределим, то по умолчанию будет вызываться
                        // стандартный AuthenticationManager
                        // который возвращает ответ, что наш юзер не валиден
                        customAuthenticationManager,

                        //Ну тут, я думаю, всё и так понятно
                        //Генерирует клаймы, токены и проверяет валидность
                        new JwtTokenProvider(), userRepository, tokenRepository))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // в таком виде позволяет пользоваться Postman
        // и вобще отправлять запросы
        // Вместо http.build() так же можно использовать 'http.formLogin(withDefaults()).build();'
        // Эта команда будет отрисовывать встроенную форму html с полями Login Password
        return http.build();
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }
}