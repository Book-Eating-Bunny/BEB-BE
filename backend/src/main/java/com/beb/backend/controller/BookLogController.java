package com.beb.backend.controller;

import com.beb.backend.dto.requestDto.AddReadBookDto;
import com.beb.backend.dto.requestDto.AddWishlistBookDto;
import com.beb.backend.dto.requestDto.CreateReviewDto;
import com.beb.backend.dto.requestDto.UpdateReviewDto;
import com.beb.backend.dto.responseDto.*;
import com.beb.backend.service.BookLogService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class BookLogController {
    private final BookLogService bookLogService;

    @GetMapping("/users/me/read-books")
    public ResponseEntity<BaseResponseDto<ReadBookListDto<UserReadBookDto>>>
    getCurrentUserReadBooks(@RequestParam(defaultValue = "1") @Min(value = 1) int page,
                            @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("readAt", "createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(bookLogService.getCurrentUserReadBooks(pageable));
    }

    @GetMapping("/users/{userId}/read-books")
    public ResponseEntity<BaseResponseDto<ReadBookListDto<UserReadBookDto>>>
    getUserReadBooks(@PathVariable @Min(value = 1) Long userId,
                     @RequestParam(defaultValue = "1") @Min(value = 1) int page,
                     @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("readAt", "createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(bookLogService.getUserReadBooks(userId, pageable));
    }

    @PostMapping("/users/me/read-books")
    public ResponseEntity<BaseResponseDto<Void>> addBookToReadBook(@RequestBody AddReadBookDto request) {
        bookLogService.addBookToReadBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.ofEmptySuccess("읽은 책 추가 성공"));
    }

    @DeleteMapping("/users/me/read-books/{readBookId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteBookFromReadBook(@PathVariable @Min(value = 1) Long readBookId) {
        bookLogService.deleteBookFromReadBook(readBookId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.ofEmptySuccess("읽은 책 삭제 성공"));
    }

    @GetMapping("/users/me/want-to-read-books")
    public ResponseEntity<BaseResponseDto<WishlistBookListDto<UserWishlistBookDto>>>
    getCurrentUserWishlistBooks(@RequestParam(defaultValue = "1") @Min(value = 1) int page,
                                @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(bookLogService.getCurrentUserWishlistBooks(pageable));
    }

    @GetMapping("/users/{userId}/want-to-read-books")
    public ResponseEntity<BaseResponseDto<WishlistBookListDto<UserWishlistBookDto>>>
    getUserWishlistBooks(@PathVariable @Min(value = 1) Long userId,
                         @RequestParam(defaultValue = "1") @Min(value = 1) int page,
                         @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(bookLogService.getUserWishlistBooks(userId, pageable));
    }

    @PostMapping("/users/me/want-to-read-books")
    public ResponseEntity<BaseResponseDto<Void>> addBookToWishlistBook(@RequestBody AddWishlistBookDto request) {
        bookLogService.addBookToWishlistBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.ofEmptySuccess("찜한 책 추가 성공"));
    }

    @DeleteMapping("/users/me/want-to-read-books/{wishlistBookId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteBookFromWishlistBook(
            @PathVariable @Min(value = 1) Long wishlistBookId) {
        bookLogService.deleteBookFromWishlistBook(wishlistBookId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.ofEmptySuccess("찜한 책 삭제 성공"));
    }

    @GetMapping("/users/me/reviews")
    public ResponseEntity<BaseResponseDto<ReviewListDto<UserReviewDto>>>
    getCurrentUserReviews(@RequestParam(defaultValue = "1") @Min(value = 1) int page,
                          @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(bookLogService.getCurrentUserReviews(pageable));
    }

    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<BaseResponseDto<ReviewListDto<UserReviewDto>>>
    getUserReviews(@PathVariable @Min(value = 1) Long userId,
                   @RequestParam(defaultValue = "1") @Min(value = 1) int page,
                   @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(bookLogService.getUserReviews(userId, pageable));
    }

    @GetMapping("/books/{bookId}/reviews")
    public ResponseEntity<BaseResponseDto<ReviewListDto<BookReviewDto>>>
    getBookReviews(@PathVariable @Min(value = 1) Long bookId,
                   @RequestParam(defaultValue = "1") @Min(value = 1) int page,
                   @RequestParam(defaultValue = "12") @Min(value = 1) int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(
                bookLogService.getBookReviews(bookId, pageable)
        );
    }

    @GetMapping("/reviews")
    public ResponseEntity<BaseResponseDto<ReviewListDto<ReviewDetailsDto>>>
    getAllReviews(@RequestParam(defaultValue = "1") @Min(value = 1) int page,
                  @RequestParam(defaultValue = "12") @Min(value = 1) int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(
                bookLogService.getAllReviews(pageable)
        );
    }

    @PostMapping("/reviews")
    public ResponseEntity<BaseResponseDto<ReviewIdDto>>
    createReview(@RequestBody @Valid CreateReviewDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseDto.ofSuccess(bookLogService.createReview(request), "리뷰 생성 성공")
        );
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponseDto<ReviewDetailsDto>>
    getReviewDetails(@PathVariable @Min(value = 1) Long reviewId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.ofSuccess(bookLogService.getReviewDetails(reviewId), "조회 성공")
        );
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponseDto<Void>>
    updateReview(@PathVariable @Min(value = 1) Long reviewId,
                 @RequestBody @Valid UpdateReviewDto request) {
        bookLogService.updateReview(reviewId, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.ofEmptySuccess("리뷰 수정 성공")
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteReview(@PathVariable @Min(value = 1) Long reviewId) {
        bookLogService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.ofEmptySuccess("리뷰 삭제 성공")
        );
    }

    @PostMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<BaseResponseDto<Void>> createReviewLike(@PathVariable @Min(value = 1) Long reviewId) {
        bookLogService.createReviewLike(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.ofEmptySuccess("리뷰 좋아요 추가")
        );
    }

    @DeleteMapping("/reviews/{reviewId}/likes")
    public ResponseEntity<BaseResponseDto<Void>> deleteReviewLike(@PathVariable @Min(value = 1) Long reviewId) {
        bookLogService.deleteReviewLike(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.ofEmptySuccess("리뷰 좋아요 취소")
        );
    }
}
