package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ReadBookExceptionInfo {
    DUPLICATE_READ_BOOK(HttpStatus.BAD_REQUEST, "이미 읽은 책에 저장된 정보"),
    READ_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "읽은 책 목록에 해당 도서가 존재하지 않음");

    private final HttpStatus status;
    private final String message;

    ReadBookExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
