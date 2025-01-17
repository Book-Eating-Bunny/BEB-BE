package com.beb.backend.dto;

public record PublicProfileDto(
        String nickname,
        String profileImgUrl) implements ProfileResponseDto{
}
