package com.beb.backend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum BookLogExceptionInfo {
    DUPLICATE_READ_BOOK(HttpStatus.BAD_REQUEST, "이미 읽은 책에 저장된 정보"),
    READ_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "읽은 책 목록에 해당 도서가 존재하지 않음"),
    READ_BOOK_FORBIDDEN(HttpStatus.FORBIDDEN, "읽은 책 기록에 대한 권한 없음"),
    CANNOT_ADD_READ_BOOK_TO_WISHLIST_BOOK(HttpStatus.BAD_REQUEST, "읽은 책을 찜한 책에 저장할 수 없음"),
    DUPLICATE_WISHLIST_BOOK(HttpStatus.BAD_REQUEST, "이미 찜한 책에 저장된 정보"),
    WISHLIST_BOOK_NOT_FOUND(HttpStatus.NOT_FOUND, "찜한 책 목록에 해당 도서가 존재하지 않음"),
    WISHLIST_BOOK_FORBIDDEN(HttpStatus.FORBIDDEN, "찜한 책 기록에 대한 권한 없음"),
    REVIEW_NOT_PUBLIC(HttpStatus.FORBIDDEN, "비공개 설정된 리뷰"),
    REVIEW_NOT_FOUND(HttpStatus.NOT_FOUND, "해당 리뷰가 존재하지 않음"),
    REVIEW_FORBIDDEN(HttpStatus.FORBIDDEN, "리뷰에 대한 권한 없음"),
    REVIEW_LIKE_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 좋아요한 리뷰"),
    REVIEW_LIKE_NOT_FOUND(HttpStatus.NOT_FOUND, "좋아요 하지 않은 리뷰");

    private final HttpStatus status;
    private final String message;

    BookLogExceptionInfo(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }
}
