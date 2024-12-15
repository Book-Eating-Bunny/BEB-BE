package com.beb.backend.dto;

import java.util.List;

public record ReviewsResponseDto<T>(
        List<T> reviews
) {
}
