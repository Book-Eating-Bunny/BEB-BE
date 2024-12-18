package com.beb.backend.common;

import com.beb.backend.dto.BaseResponseDto;
import com.beb.backend.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // RequestParam에서 필수 파라미터가 없을 경우
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseDto.fail("잘못된 요청"));
    }

    // 파라미터에서 @Valid로 검사했을 때 예외 발생 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        BaseResponseDto<Object> response = BaseResponseDto.fail("잘못된 요청");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleValidationExceptions(ConstraintViolationException e) {
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

    @ExceptionHandler(BookLogException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleReadBookExceptions(BookLogException e) {
        BookLogExceptionInfo info = e.getInfo();
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.fail(info.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleBadCredentialsExceptions(BadCredentialsException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponseDto.fail("인증 실패"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleExpiredJwtExceptions(ExpiredJwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponseDto.fail("만료된 토큰"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto<Void>> handleExceptions(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponseDto.fail(e.getMessage()));
    }
}
