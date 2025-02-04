package com.beb.backend.dto.externalApiDto;

import com.beb.backend.common.ValidationRegexConstants;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinBookItemDto(
        @NotBlank String title,
        @NotBlank String author,
        LocalDate pubDate,
        @Pattern(regexp = ValidationRegexConstants.ISBN_REGEX) String isbn13,
        String cover,
        String categoryName,
        String publisher
) {
}
