package com.beb.backend.dto.requestDto;

import jakarta.validation.constraints.NotBlank;

public record CommentContentDto(@NotBlank String content) implements EditCommentRequestDto {
}
