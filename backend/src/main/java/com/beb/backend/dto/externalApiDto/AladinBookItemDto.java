package com.beb.backend.dto.externalApiDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinBookItemDto(
        @NotBlank String title,
        @NotBlank String author,
        LocalDate pubDate,
        String isbn13,
        String cover,
        String categoryName,
        String publisher
) {
}
