package com.beb.backend.dto;

import jakarta.validation.constraints.*;

public record CreateReviewRequestDto(
        @NotNull Long bookId,

        @NotNull @Min(value = 0) @Max(value = 5)
        Integer rating,

        @NotBlank @Size(min = 15)
        String content,

        @NotNull Boolean isSpoiler,
        @NotNull Boolean isPublic
) {
}
