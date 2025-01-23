package com.beb.backend.dto.requestDto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateReviewDto(
        @Min(value = 0) @Max(value = 5)
        Integer rating,

        @NotBlank @Size(min = 15)
        String content,

        Boolean isSpoiler,
        Boolean isPublic) {
}
