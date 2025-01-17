package com.beb.backend.dto;

import com.beb.backend.domain.Member;

public record FullProfileDto(
        String email,
        String nickname,
        Integer age,
        Member.Gender gender,
        String profileImgUrl) implements ProfileResponseDto{ }
