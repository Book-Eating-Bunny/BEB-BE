package com.beb.backend.dto;

public record PublicProfileDto(
        String nickname,
        String profileImgPath) implements ProfileResponseDto{
}
