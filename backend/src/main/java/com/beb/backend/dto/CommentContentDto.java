package com.beb.backend.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentContentDto(@NotBlank String content) {
}
