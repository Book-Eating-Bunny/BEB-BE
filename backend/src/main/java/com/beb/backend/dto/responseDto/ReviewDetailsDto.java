package com.beb.backend.dto.responseDto;

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
) implements BaseReviewDetailsDto { }
