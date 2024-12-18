package com.beb.backend.dto;

import java.util.List;

public record BooksResponseDto<T>(
        List<T> books
) {
}
