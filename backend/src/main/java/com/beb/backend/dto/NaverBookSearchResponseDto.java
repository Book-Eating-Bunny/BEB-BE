package com.beb.backend.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record NaverBookSearchResponseDto(
        Integer total,          // 총 검색 결과 개수
        Integer start,          // 검색 시작 위치
        Integer display,        // 한 번에 표시할 검색 결과 개수
        List<NaverBookItemDto> items
) {
}
