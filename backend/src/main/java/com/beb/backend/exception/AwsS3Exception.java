package com.beb.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AwsS3Exception extends RuntimeException {
    private final AwsS3ExceptionInfo info;
}
