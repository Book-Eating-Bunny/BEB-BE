package com.beb.backend.dto;

import java.math.BigDecimal;

public record SearchBookInfoDto(
        String isbn,
        String coverImgUrl,
        String title,
        String author,
        BigDecimal averageRating,
        Integer reviewCount
) {
}
