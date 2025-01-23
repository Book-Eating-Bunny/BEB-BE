package com.beb.backend.dto.externalApiDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverBookItemDto(
        String title,       // 제목
        String image,       // 섬네일 이미지 url
        String author,      // 저자
        String isbn         // ISBN
) {
}
