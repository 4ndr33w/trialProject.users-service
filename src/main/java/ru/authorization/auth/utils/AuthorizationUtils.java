package ru.authorization.auth.utils;

import io.jsonwebtoken.Claims;
import ru.authorization.auth.components.JwtTokenProvider;
import ru.authorization.auth.models.enums.UserStatus;

public class AuthorizationUtils {
    private final JwtTokenProvider tokenProvider;

    public AuthorizationUtils(JwtTokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    public Boolean isValidToken(String token) {

        return tokenProvider.validateToken(token);
    }

    public Boolean isAuthorized(String token) {
        return isValidToken(token);
    }


    public Claims validateTokenAndGetClaims(String token) {
        return tokenProvider.getClaims(token);
    }

    public boolean isAdmin(Claims claims) {
        var userStatus = UserStatus.valueOf(claims.get("roles", String.class));
        if(userStatus.equals(UserStatus.ADMIN)) {
            return true;
        } else {
            return false;
        }
    }
    public String getUserName(Claims claims) {
        return claims.get("sub", String.class);
    }
}
