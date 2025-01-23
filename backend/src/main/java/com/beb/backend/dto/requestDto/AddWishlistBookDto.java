package com.beb.backend.dto.requestDto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record AddWishlistBookDto(@NotNull @Min(value = 1) Long bookId) {
}
