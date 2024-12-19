package com.beb.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OpenApiException extends RuntimeException {
    private final OpenApiExceptionInfo info;
}
