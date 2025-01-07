package com.beb.backend.dto;

import java.time.LocalDateTime;

public record UserWishlistBookDto(
        Long wishlistBookId,
        BookSummaryDto book,
        LocalDateTime createdAt
) {
}
