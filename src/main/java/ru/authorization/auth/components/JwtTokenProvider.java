package ru.authorization.auth.components;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.utils.StaticResources;
import ru.authorization.auth.utils.exceptions.global.GlobalExceptionHandler;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    public String getToken(UserModel userDetails) {
        log.info("Getting token from UserModel");
        return Jwts.builder()
                .setSubject(userDetails.getEmail())
                .claim("roles", userDetails.getUserStatus().toString())
                .setId(String.valueOf(userDetails.getId()))
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + StaticResources.JWT_EXPIRATION_MS))
                .signWith(
                        SignatureAlgorithm.HS256,
                        StaticResources.JWT_SECRET.getBytes(StandardCharsets.UTF_8)
                )
                .compact();
    }

    public String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public String getUsernameFromToken(String token) {
        log.info("Getting username from JWT token");
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = validateAndExtractClaims(token);
        log.info("Successfully validated token");
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        log.info("Check isTokenExpired?");
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        log.info("Getting expiration date from JWT token");
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDto userDto) {
        final String username = getUsernameFromToken(token);
        if(username == null) {
            return false;
        }
        log.info("Validating JWT token");
        return (username.equals(userDto.getEmail()) && !isTokenExpired(token));
    }

    public boolean validateToken(String token) {
        final String username = getUsernameFromToken(token);
        if(username == null) {
            return false;
        }
        log.info("Validating JWT token");
        return (!isTokenExpired(token));
    }

    public Claims validateAndExtractClaims(String token) {

        Key key = Keys.hmacShaKeyFor(StaticResources.JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        try {
            log.info("Trying to validate JWT token");
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("JWT token is invalid");
            return new DefaultClaims(new HashMap<String, Object>());
        }
    }

    public Authentication getAuthentication(String token) {
        String username = getUsernameFromToken(token);
        return new UsernamePasswordAuthenticationToken(username, "", Collections.emptyList());
    }
}