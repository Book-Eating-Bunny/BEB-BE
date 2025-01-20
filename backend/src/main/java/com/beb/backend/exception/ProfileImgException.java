package com.beb.backend.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ProfileImgException extends RuntimeException {
    private final ProfileImgExceptionInfo info;
}
