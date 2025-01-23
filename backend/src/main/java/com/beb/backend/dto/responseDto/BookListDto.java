package com.beb.backend.dto.responseDto;

import java.util.List;

public record BookListDto<T>(
        List<T> books
) {
}
