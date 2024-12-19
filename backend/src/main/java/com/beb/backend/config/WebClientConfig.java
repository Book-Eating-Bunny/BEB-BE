package com.beb.backend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final Environment env;

    @Value("${open-api.aladin.ttbkey}")
    private String TTBKEY;

    @Bean("naverWebClient")
    public WebClient naverWebClient(WebClient.Builder builder) {
        return builder.baseUrl("https://openapi.naver.com/v1/search")
                .defaultHeader("X-Naver-Client-Id", env.getProperty("open-api.naver.client-id"))
                .defaultHeader("X-Naver-Client-Secret", env.getProperty("open-api.naver.client-secret"))
                .build();
    }

    @Bean("aladinWebClient")
    public WebClient aladinWebClient(WebClient.Builder builder) {
        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("http://www.aladin.co.kr/ttb/api");
        factory.setDefaultUriVariables(Map.of("ttbkey", TTBKEY));

        return builder.uriBuilderFactory(factory).build();
    }
}
