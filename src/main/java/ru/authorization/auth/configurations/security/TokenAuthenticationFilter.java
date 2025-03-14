package ru.authorization.auth.configurations.security;

import ru.authorization.auth.components.JwtTokenProvider;

public class TokenAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;

    public TokenAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
}
