package com.beb.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddWishlistBookRequestDto(@NotNull @Min(value = 1) Long bookId) {
}
