package com.beb.backend.dto;

import org.springframework.lang.NonNull;

public record LoginRequestDto(
        @NonNull String email,
        @NonNull String password
) {
}
