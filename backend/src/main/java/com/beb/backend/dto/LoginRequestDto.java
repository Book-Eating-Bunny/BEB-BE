package com.beb.backend.dto;

import jakarta.validation.constraints.NotNull;

public record LoginRequestDto(
        @NotNull String email,
        @NotNull String password
) {
}
