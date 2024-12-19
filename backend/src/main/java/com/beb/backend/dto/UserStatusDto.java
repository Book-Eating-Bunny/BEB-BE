package com.beb.backend.dto;

public record UserStatusDto(
        boolean isWishlist,
        boolean isRead,
        boolean hasReview
) {
}
