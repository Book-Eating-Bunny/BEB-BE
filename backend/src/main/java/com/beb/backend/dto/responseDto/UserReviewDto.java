package com.beb.backend.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

public record UserReviewDto(
        Long reviewId,
        BookSummaryDto book,
        Integer rating,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        @JsonInclude(JsonInclude.Include.NON_NULL) Boolean isSpoiler
) implements BaseReviewDetailsDto { }