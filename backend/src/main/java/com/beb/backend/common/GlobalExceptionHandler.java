package com.beb.backend.common;

import com.beb.backend.dto.responseDto.BaseResponseDto;
import com.beb.backend.exception.*;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // RequestParam에서 필수 파라미터가 없을 경우
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("Missing Parameter Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseDto.ofFailure("잘못된 요청"));
    }

    // 파라미터에서 @Valid로 검사했을 때 예외 발생 시
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.warn("Validation Failed: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseDto.ofFailure("잘못된 요청"));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponseDto<Object>> handleValidationExceptions(ConstraintViolationException e) {
        log.warn("Validation Failed: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(BaseResponseDto.ofFailure("잘못된 요청"));
    }

    @ExceptionHandler(MemberException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleMemberExceptions(MemberException e) {
        MemberExceptionInfo info = e.getInfo();
        log.warn("{}. Status: {}", info.getMessage(), info.getStatus(), e);
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.ofFailure(info.getMessage()));
    }

    @ExceptionHandler(ProfileImgException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleProfileImgExceptions(ProfileImgException e) {
        ProfileImgExceptionInfo info = e.getInfo();
        log.warn("{}. Status: {}", info.getMessage(), info.getStatus(), e);
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.ofFailure(info.getMessage()));
    }

    @ExceptionHandler(BookException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleBookExceptions(BookException e) {
        BookExceptionInfo info = e.getInfo();
        log.warn("{}. Status: {}", info.getMessage(), info.getStatus(), e);
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.ofFailure(info.getMessage()));
    }

    @ExceptionHandler(BookLogException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleReadBookExceptions(BookLogException e) {
        BookLogExceptionInfo info = e.getInfo();
        log.warn("{}. Status: {}", info.getMessage(), info.getStatus(), e);
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.ofFailure(info.getMessage()));
    }

    @ExceptionHandler(CommentException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleCommentExceptions(CommentException e) {
        CommentExceptionInfo info = e.getInfo();
        log.warn("{}. Status: {}", info.getMessage(), info.getStatus(), e);
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.ofFailure(info.getMessage()));
    }

    @ExceptionHandler(OpenApiException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleOpenApiExceptions(OpenApiException e) {
        OpenApiExceptionInfo info = e.getInfo();
        log.warn("{}. Status: {}", info.getMessage(), info.getStatus(), e);
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.ofFailure(info.getMessage()));
    }

    @ExceptionHandler(AwsS3Exception.class)
    public ResponseEntity<BaseResponseDto<Void>> handleAwsS3Exceptions(AwsS3Exception e) {
        AwsS3ExceptionInfo info = e.getInfo();
        return ResponseEntity.status(info.getStatus()).body(BaseResponseDto.ofFailure(info.getMessage()));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleBadCredentialsExceptions(BadCredentialsException e) {
        log.warn("Bad Credentials Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponseDto.ofFailure("인증 실패"));
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleExpiredJwtExceptions(ExpiredJwtException e) {
        log.warn("Expired JWT Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponseDto.ofFailure("만료된 토큰"));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<BaseResponseDto<Void>> handleAuthenticationExceptions(AuthenticationException e) {
        log.warn("Authentication Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(BaseResponseDto.ofFailure("인증 실패"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<BaseResponseDto<Void>> handleExceptions(Exception e) {
        log.error("Unexpected Exception: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(BaseResponseDto.ofFailure(e.getMessage()));
    }
}
