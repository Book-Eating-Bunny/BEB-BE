package com.beb.backend.dto.responseDto;

import java.time.LocalDateTime;

public record UserWishlistBookDto(
        Long wishlistBookId,
        BookSummaryDto book,
        LocalDateTime createdAt
) {
}
