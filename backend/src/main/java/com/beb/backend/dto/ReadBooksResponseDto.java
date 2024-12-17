package com.beb.backend.dto;

import java.util.List;

public record ReadBooksResponseDto<T>(
        List<T> readBooks
) {
}
