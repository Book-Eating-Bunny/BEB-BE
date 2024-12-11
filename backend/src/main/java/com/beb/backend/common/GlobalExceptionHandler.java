package com.beb.backend.common;

import com.beb.backend.dto.BaseResponseDto;
import com.beb.backend.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // 파라미터에서 @Valid로 검사했을 때 예외 발생 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        BaseResponseDto<Object> response = BaseResponseDto.fail("잘못된 요청");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleMemberExceptions(MemberException e) {
        MemberExceptionInfo info = e.getInfo();
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.fail(info.getMessage()));
    }

    @ExceptionHandler(BookException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleBookExceptions(BookException e) {
        BookExceptionInfo info = e.getInfo();
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.fail(info.getMessage()));
    }

    @ExceptionHandler(ReadBookException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleReadBookExceptions(ReadBookException e) {
        ReadBookExceptionInfo info = e.getInfo();
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.fail(info.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto<Void>> handleExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponseDto.fail(e.getMessage()));
    }
}
