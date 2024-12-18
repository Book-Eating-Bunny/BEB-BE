package com.beb.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final Environment env;

    @Bean("naverWebClient")
    public WebClient naverWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://openapi.naver.com/v1/search")
                .defaultHeader("X-Naver-Client-Id", env.getProperty("open-api.naver.client-id"))
                .defaultHeader("X-Naver-Client-Secret", env.getProperty("open-api.naver.client-secret"))
                .build();
    }

    @Bean("aladinWebClient")
    public WebClient aladinWebClient(WebClient.Builder builder) {
        return builder.baseUrl("http://www.aladin.co.kr/ttb/api").build();
    }
}
