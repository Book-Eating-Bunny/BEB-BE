package com.beb.backend.dto.requestDto;

import com.beb.backend.common.ValidationRegexConstants;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record LoginDto(
        @NotNull @Pattern(
                regexp = ValidationRegexConstants.EMAIL_REGEX,
                message = "잘못된 이메일 형식")
        String email,

        @NotNull @Pattern(
                regexp = ValidationRegexConstants.PASSWORD_REGEX,
                message = "비밀번호는 8-20자 사이의 숫자, 영어 또는 특수문자(!@#$%^&*)로만 이루어져야 합니다.")
        String password
) {
}
