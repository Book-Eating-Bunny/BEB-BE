package com.beb.backend.dto;

import com.beb.backend.domain.Member;
import lombok.NonNull;

public record SignUpRequestDto(
        @NonNull String email,
        @NonNull String password,
        @NonNull String nickname,
        @NonNull Integer age,
        @NonNull Member.Gender gender,
        String profileImgPath
) { }
