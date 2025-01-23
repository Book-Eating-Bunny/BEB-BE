package com.beb.backend.dto.responseDto;

public record PublicProfileDto(
        String nickname,
        String profileImgUrl) implements ProfileResponseDto{
}
