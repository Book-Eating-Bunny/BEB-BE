package com.beb.backend.dto;

import com.beb.backend.domain.Member;

public record ProfileResponseDto(
        String email,
        String nickname,
        Integer age,
        Member.Gender gender,
        String profileImgPath) { }
