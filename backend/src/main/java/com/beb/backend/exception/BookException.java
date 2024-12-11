package com.beb.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BookException extends RuntimeException {
    private final BookExceptionInfo info;
}
