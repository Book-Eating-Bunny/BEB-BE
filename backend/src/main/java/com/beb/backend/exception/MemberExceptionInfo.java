package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberExceptionInfo {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없음"),
    DUPLICATE_EMAIL(HttpStatus.BAD_REQUEST, "중복된 이메일"),
    DUPLICATE_NICKNAME(HttpStatus.BAD_REQUEST, "중복된 닉네임"),
    EMAIL_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 이메일 형식"),
    NICKNAME_NOT_VALID(HttpStatus.BAD_REQUEST, "잘못된 닉네임 형식");

    private final HttpStatus status;
    private final String message;

    MemberExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
