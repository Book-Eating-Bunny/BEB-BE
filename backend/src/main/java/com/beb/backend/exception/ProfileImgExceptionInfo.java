package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProfileImgExceptionInfo {
    NULL_OR_EMPTY_FILE(HttpStatus.BAD_REQUEST, "빈 파일"),
    INVALID_FILE_EXTENSION(HttpStatus.BAD_REQUEST, "유효하지 않은 파일 확장자"),
    FILE_SIZE_LIMIT_EXCEEDED(HttpStatus.BAD_REQUEST, "파일 허용 크기 초과");

    private final HttpStatus status;
    private final String message;

    ProfileImgExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
