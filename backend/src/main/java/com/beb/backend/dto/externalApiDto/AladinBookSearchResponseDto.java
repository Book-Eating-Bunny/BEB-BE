package com.beb.backend.dto.externalApiDto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AladinBookSearchResponseDto(
        @NotNull Integer totalResults,
        @NotNull Integer startIndex,
        @NotNull Integer itemsPerPage,
        @NotNull List<AladinBookItemDto> item
) {
}
