package com.beb.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CurrentUserReadBookDto(
        Long readBookId,
        BookSummaryDto book,
        LocalDate readAt,
        LocalDateTime createdAt
) {
}
