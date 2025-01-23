package com.beb.backend.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDate;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserReadBookDto(
        Long readBookId,
        BookSummaryDto book,
        LocalDate readAt,
        LocalDateTime createdAt
) {
}
