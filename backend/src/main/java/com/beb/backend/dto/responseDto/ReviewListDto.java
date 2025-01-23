package com.beb.backend.dto.responseDto;

import java.util.List;

public record ReviewListDto<T>(
        List<T> reviews
) {
}
