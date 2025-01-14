package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum CommentExceptionInfo {
    COMMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 댓글이 존재하지 않음"),
    COMMENT_FORBIDDEN(HttpStatus.FORBIDDEN, "댓글에 대한 권한 없음");

    private final HttpStatus status;
    private final String message;

    CommentExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
