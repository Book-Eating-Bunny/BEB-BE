package com.beb.backend.dto;

import java.time.LocalDateTime;

public record CurrentUserWishlistBookDto(
        Long readBookId,
        BookSummaryWithRatingDto book,
        LocalDateTime createdAt
) {
}
