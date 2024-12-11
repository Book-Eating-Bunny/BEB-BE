package com.beb.backend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

import java.time.LocalDate;

public record AddReadBookRequestDto(
        @NotNull @Min(value = 1) Long bookId,
        @PastOrPresent LocalDate readAt) {
}
