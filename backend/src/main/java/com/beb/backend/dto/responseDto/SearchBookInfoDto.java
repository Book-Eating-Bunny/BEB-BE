package com.beb.backend.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

public record SearchBookInfoDto(
        String isbn,
        String coverImgUrl,
        String title,
        String author,
        @JsonInclude(JsonInclude.Include.NON_NULL) Long bookId,
        BigDecimal averageRating,
        Integer reviewCount
) {
}
