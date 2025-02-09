package com.beb.backend.dto.responseDto;

import java.time.LocalDateTime;

public record CommentDetailsDto(
        Long commentId,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        MemberSummaryDto user
) {
}
