package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum MemberExceptionInfo {
    MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 회원을 찾을 수 없음");

    private final HttpStatus status;
    private final String message;

    MemberExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
