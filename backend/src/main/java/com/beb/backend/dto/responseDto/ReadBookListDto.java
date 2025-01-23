package com.beb.backend.dto.responseDto;

import java.util.List;

public record ReadBookListDto<T>(
        List<T> readBooks
) {
}
