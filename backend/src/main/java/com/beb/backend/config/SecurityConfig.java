package com.beb.backend.config;

import com.beb.backend.auth.BebAuthenticationProvider;
import com.beb.backend.auth.JwtValidatorFilter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final Environment env;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity
                .csrf((csrfConfig) -> csrfConfig.disable())
                .cors(corsConfig -> corsConfig.configurationSource(new CorsConfigurationSource() {
                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {
                        CorsConfiguration config = new CorsConfiguration();
                        config.setAllowedOrigins(Collections.singletonList("http://localhost:3000"));   // 허용할 IP
                        config.setAllowedMethods(Collections.singletonList("*"));
                        config.setAllowCredentials(true);   // 인증 관련 쿠키를 포함한 요청 허용 여부
                        config.setAllowedHeaders(Collections.singletonList("*"));
                        config.setMaxAge(3600L);            // 브라우저가 CORS 정책을 캐싱하는 시간(초 단위)
                        return config;
                    }
                }))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/error",
                                "/api/v1/users/signup",
                                "/api/v1/users/login",
                                "/api/v1/users/reissue",
                                "/api/v1/users/email-availability",
                                "/api/v1/users/nickname-availability").permitAll()
                        .anyRequest().authenticated() // 나머지는 인증 필요
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(new JwtValidatorFilter(env), BasicAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        BebAuthenticationProvider authenticationProvider = new BebAuthenticationProvider(userDetailsService, passwordEncoder);
        return new ProviderManager(authenticationProvider);
    }
}
