package com.beb.backend.dto;

import java.time.LocalDateTime;

public record CurrentUserReviewDto(
        Long reviewId,
        BookSummaryDto book,
        Integer rating,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }