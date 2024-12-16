package com.beb.backend.dto;

import java.time.LocalDateTime;

public record ReviewDetailsResponseDto(
        Long reviewId,
        BookSummaryDto book,
        MemberSummaryDto user,
        Integer rating,
        String content,
        Boolean isSpoiler,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
