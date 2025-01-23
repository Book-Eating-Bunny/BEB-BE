package com.beb.backend.dto.responseDto;

import java.time.LocalDateTime;

public record BookReviewDto(
        Long reviewId,
        MemberSummaryDto user,
        Integer rating,
        String content,
        Boolean isSpoiler,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) implements BaseReviewDetailsDto { }