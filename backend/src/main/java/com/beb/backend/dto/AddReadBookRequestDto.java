package com.beb.backend.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record AddReadBookRequestDto(
        @NotNull Long bookId,
        @PastOrPresent LocalDate readAt) {
}
