package ru.authorization.auth.components;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;

import ru.authorization.auth.models.UserModel;

import java.util.Date;
import java.util.function.Function;

@Component
public class JwtTokenProvider {

    private final String JWT_SECRET = "yourSecretKeyShouldBeLongerAndMoreComplex";
    private final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 24; // 24 hours in milliseconds

    public String createToken(UserModel userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getEmail())
                .claim("roles", userDetails.getUserStatus().toString())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(SignatureAlgorithm.HS256, JWT_SECRET.getBytes())
                .compact();
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    private Claims getAllClaimsFromToken(String token) {

        try {
            return Jwts.parser().setSigningKey(JWT_SECRET).build()
                    .parseSignedClaims(token)
                    .getPayload();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Invalid JWT signature");
        }
    }

    public boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    public boolean validateToken(String token, UserModel userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
