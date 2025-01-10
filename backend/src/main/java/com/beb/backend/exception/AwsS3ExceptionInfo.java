package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AwsS3ExceptionInfo {
    FILE_UPLOAD_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드 실패"),
    FILE_DELETE_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "파일 삭제 실패");

    private final HttpStatus status;
    private final String message;

    AwsS3ExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
