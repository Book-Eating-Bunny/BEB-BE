package com.beb.backend.dto.responseDto;

public record UserStatusDto(
        boolean isWishlist,
        boolean isRead,
        boolean hasReview
) {
}
