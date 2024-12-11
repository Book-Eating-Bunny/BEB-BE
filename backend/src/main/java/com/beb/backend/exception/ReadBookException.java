package com.beb.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReadBookException extends RuntimeException {
    private final ReadBookExceptionInfo info;
}
