package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReadBookExceptionInfo {
    DUPLICATE_READ_BOOK(HttpStatus.BAD_REQUEST, "이미 읽은 책에 저장된 정보");

    private final HttpStatus status;
    private final String message;

    ReadBookExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
