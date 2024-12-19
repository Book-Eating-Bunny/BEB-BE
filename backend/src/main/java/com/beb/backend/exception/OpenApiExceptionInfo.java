package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum OpenApiExceptionInfo {
    API_CALL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "외부 API 호출 에러"),
    ALADIN_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 도서가 존재하지 않음");

    private final HttpStatus status;
    private final String message;

    OpenApiExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
