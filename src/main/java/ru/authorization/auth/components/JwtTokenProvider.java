package ru.authorization.auth.components;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import ru.authorization.auth.models.Dtos.UserDto;
import ru.authorization.auth.models.UserModel;
import ru.authorization.auth.utils.StaticResources;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    public String getToken(UserModel userDetails) {
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
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {

        final Claims claims = validateAndExtractClaims(token);
        return claimsResolver.apply(claims);
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserDto userDto) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDto.getEmail()) && !isTokenExpired(token));
    }

    public Claims validateAndExtractClaims(String token) {

        Key key = Keys.hmacShaKeyFor(StaticResources.JWT_SECRET.getBytes(StandardCharsets.UTF_8));

        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }
    }
}