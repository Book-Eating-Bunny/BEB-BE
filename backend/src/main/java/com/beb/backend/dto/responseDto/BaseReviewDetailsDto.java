package com.beb.backend.dto.responseDto;

import java.time.LocalDateTime;

public interface BaseReviewDetailsDto {
    Long reviewId();
    Integer rating();
    String content();
    Boolean isSpoiler();
    LocalDateTime createdAt();
    LocalDateTime updatedAt();
}
