package com.beb.backend.dto;

import java.time.LocalDateTime;

public record CurrentUserWishlistBookDto(
        Long wishlistBookId,
        BookSummaryWithRatingDto book,
        LocalDateTime createdAt
) {
}
