package com.beb.backend.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtGenerator {
    private static final String ISSUER = "Book-Eating-Bunny";

    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60L * 60L * 2L;         // 2시간
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60L * 60L * 24L * 7L;  // 7일

    @Value("${jwt.secret-key}")
    private String SECRET_KEY;

    public String createAccessToken(String username) {
        return createToken(username, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String username) {
        return createToken(username, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String createToken(String username, Long expireTime) {
        SecretKey secretKey = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .issuer(ISSUER)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey).compact();
    }
}
