package com.beb.backend.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtils {     // 토큰 생성, 유효성 검사
    private static final String ISSUER = "Book-Eating-Bunny";

//    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60L * 60L * 2L;         // 2시간
    private static final Long ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60L * 60L * 24L * 60L;  // 60일(임시)
    private static final Long REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60L * 60L * 24L * 7L;  // 7일

    private final SecretKey secretKey;

    private JwtUtils(@Value("${jwt.secret-key}") String keyString) {
        this.secretKey = Keys.hmacShaKeyFor(keyString.getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String username) {
        return createToken(username, ACCESS_TOKEN_EXPIRE_TIME);
    }

    public String createRefreshToken(String username) {
        return createToken(username, REFRESH_TOKEN_EXPIRE_TIME);
    }

    public String createToken(String username, Long expireTime) {
        return Jwts.builder()
                .header().add("typ", "JWT").and()
                .issuer(ISSUER)
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(secretKey).compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build()
                    .parseSignedClaims(token).getPayload()
                    .getSubject();
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid token");
        } catch (Exception e) {
            throw new RuntimeException("Unexpected error: " + e.getMessage());
        }
    }
}
