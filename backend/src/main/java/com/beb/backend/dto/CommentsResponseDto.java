package com.beb.backend.dto;

import java.util.List;

public record CommentsResponseDto<T>(
        List<T> comments
) {
}
