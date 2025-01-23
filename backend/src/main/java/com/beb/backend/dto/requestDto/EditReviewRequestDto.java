package com.beb.backend.dto.requestDto;

public interface EditReviewRequestDto {
    Integer rating();
    String content();
    Boolean isSpoiler();
    Boolean isPublic();
}
