package com.beb.backend.dto;

import java.time.LocalDateTime;

public record ReviewDetailsDto(
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
