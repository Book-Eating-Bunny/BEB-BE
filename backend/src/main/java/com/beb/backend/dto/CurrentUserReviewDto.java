package com.beb.backend.dto;

import java.time.LocalDateTime;

public record CurrentUserReviewDto(
        Long reviewId,
        BookSummaryDto bookSummaryDto,
        Integer rating,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) { }