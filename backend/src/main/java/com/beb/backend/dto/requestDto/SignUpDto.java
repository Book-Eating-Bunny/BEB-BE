package com.beb.backend.dto.requestDto;

import com.beb.backend.common.ValidationRegexConstants;
import com.beb.backend.domain.Member;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record SignUpDto(
        @NotNull @Pattern(
                regexp = ValidationRegexConstants.EMAIL_REGEX,
                message = "잘못된 이메일 형식")
        String email,

        @NotNull @Pattern(
                regexp = ValidationRegexConstants.PASSWORD_REGEX,
                message = "비밀번호는 8-20자 사이의 숫자, 영어 또는 특수문자(!@#$%^&*)로만 이루어져야 합니다.")
        String password,

        @NotNull @Pattern(
                regexp = ValidationRegexConstants.NICKNAME_REGEX,
                message = "닉네임은 8자 이하의 한글, 영어, 숫자만 허용됩니다.")
        String nickname,

        @NotNull @Min(value = 1) @Max(value = 100)
        Integer age,

        @NotNull @Enumerated(EnumType.STRING)
        Member.Gender gender
) { }
