package ru.authorization.auth.components;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.utils.StaticResources;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtTokenProvider {

    public String getToken(UserModel userDetails) {
        log.info("-------------------------\nJwtTokenProvider.getToken\nВход в метод, генерируем токен {}\n-------------------------", userDetails.getEmail());
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

    public String getUsernameFromToken(String token) {
        log.info("-------------------------\nJwtTokenProvider.getUsernameFromToken\nGetting username from token\n-------------------------");
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = validateAndExtractClaims(token);
        log.info("-------------------------\nJwtTokenProvider.getClaimFromToken\nGetting claims from token\n-------------------------");
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        log.info("-------------------------\nJwtTokenProvider.isTokenExpired\nChecking if token is expired\n-------------------------");
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        log.info("-------------------------\nJwtTokenProvider.getExpirationDateFromToken\nGetting expiration date from token\n-------------------------");
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDto userDto) {
        final String username = getUsernameFromToken(token);
        log.info("-------------------------\nJwtTokenProvider.validateToken\nValidating token (Boolean)\n-------------------------");
        return (username.equals(userDto.getEmail()) && !isTokenExpired(token));
    }

    public Claims validateAndExtractClaims(String token) {

        Key key = Keys.hmacShaKeyFor(StaticResources.JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        try {
            log.info("-------------------------\nJwtTokenProvider.validateAndExtractClaims\nValidating token: return Claims\n-------------------------");
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            log.error("-------------------------\nJwtTokenProvider.validateAndExtractClaims\nToken is not valid. Invalid JWT signature: {}\n-------------------------", e.getMessage());
            throw new IllegalArgumentException("Invalid JWT signature");
        }
    }
}