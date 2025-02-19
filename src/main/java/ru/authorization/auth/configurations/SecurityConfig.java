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
import ru.authorization.auth.repositories.UserRepository;
import ru.authorization.auth.services.CustomUserDetailsService;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private CustomUserDetailsService customUserDetailsService;
    private final UserRepository userRepository;
    //private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private CustomAuthenticationManager customAuthenticationManager;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, CustomAuthenticationManager customAuthenticationManager) throws Exception {

        String adminRole = UserStatus.ADMIN.toString();
        String userRole = UserStatus.USER.toString();

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        //.requestMatchers(HttpMethod.POST,"/users/**").permitAll()
                        .requestMatchers(HttpMethod.GET,"/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/**").permitAll()
                        //.requestMatchers(HttpMethod.GET,"/users/**").permitAll()
                        //.requestMatchers(HttpMethod.POST,"/users/token").permitAll()
                        //.requestMatchers(HttpMethod.GET,"/users/token").permitAll()
                        //.requestMatchers(HttpMethod.GET,"/**").permitAll()
                        /* .requestMatchers(HttpMethod.POST,"/login").permitAll()
                         .requestMatchers(HttpMethod.POST,"/users/create").permitAll()
                         .requestMatchers(HttpMethod.GET,"/users/mail/*").permitAll()
                         .requestMatchers(HttpMethod.GET,"/users/*").permitAll()
                         .requestMatchers(HttpMethod.POST,"/users/*").permitAll()
                         .requestMatchers(HttpMethod.POST,"/users/**").permitAll()
                         .requestMatchers("/account").hasRole(adminRole)
                         .requestMatchers("/delete**").hasRole(adminRole)
                         .requestMatchers("/mail**").hasRole(adminRole)*/
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults())
                .addFilter(new AuthenticationFilter(customAuthenticationManager, new JwtTokenProvider(), userRepository))
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
/*
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }*/
/*
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }*/

    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService);
    }
}
