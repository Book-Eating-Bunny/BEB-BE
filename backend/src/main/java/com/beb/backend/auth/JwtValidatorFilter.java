package com.beb.backend.auth;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class JwtValidatorFilter extends OncePerRequestFilter {

    private final Environment env;
    private static final String BEARER_PREFIX = "Bearer ";

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            final String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authorization == null || !authorization.startsWith(BEARER_PREFIX)) {
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authorization.split(" ")[1];

            SecretKey secretKey = Keys.hmacShaKeyFor(
                    env.getProperty("jwt.secret-key").getBytes(StandardCharsets.UTF_8)
            );

            Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(jwt).getPayload();

            String username = claims.getSubject();
            // TODO: 권한 부여
            Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, null);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (ExpiredJwtException e) {
            throw new BadCredentialsException("Expired JWT.");
        } catch (JwtException e) {
            throw new BadCredentialsException("Invalid JWT. " + e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // 로그인 API path 경로와 같을 때 필터가 실행 x
        return request.getServletPath().equals("/api/v1/users/login");
    }
}
