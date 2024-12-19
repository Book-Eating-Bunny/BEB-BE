package com.beb.backend.controller;

import com.beb.backend.domain.Member;
import com.beb.backend.dto.*;
import com.beb.backend.service.BookLogService;
import com.beb.backend.service.MemberService;
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
    private final MemberService memberService;

    @GetMapping("/users/me/read-books")
    public ResponseEntity<BaseResponseDto<ReadBooksResponseDto<CurrentUserReadBookDto>>>
    getCurrentUserReadBooks(@RequestParam(defaultValue = "1") @Min(value = 1) int page,
                            @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Member member = memberService.getCurrentMember();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("readAt", "createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(
                bookLogService.getUserReadBooksById(member, pageable)
        );
    }

    @PostMapping("/users/me/read-books")
    public ResponseEntity<BaseResponseDto<Void>> addBookToReadBook(@RequestBody AddReadBookRequestDto request) {
        bookLogService.addBookToReadBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.emptySuccess("읽은 책 추가 성공"));
    }

    @DeleteMapping("/users/me/read-books/{readBookId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteBookFromReadBook(@PathVariable @Min(value = 1) Long readBookId) {
        bookLogService.deleteBookFromReadBook(readBookId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.emptySuccess("읽은 책 삭제 성공"));
    }

    @GetMapping("/users/me/want-to-read-books")
    public ResponseEntity<BaseResponseDto<WishlistBooksResponseDto<CurrentUserWishlistBookDto>>>
    getCurrentUserWishlistBooks(@RequestParam(defaultValue = "1") @Min(value = 1) int page,
                                @RequestParam(defaultValue = "12") @Min(value = 1) int size) {

        Member member = memberService.getCurrentMember();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(
                bookLogService.getUserWishlistBooksById(member, pageable)
        );
    }

    @PostMapping("/users/me/want-to-read-books")
    public ResponseEntity<BaseResponseDto<Void>> addBookToWishlistBook(@RequestBody AddWishlistBookRequestDto request) {
        bookLogService.addBookToWishlistBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.emptySuccess("찜한 책 추가 성공"));
    }

    @DeleteMapping("/users/me/want-to-read-books/{wishlistBookId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteBookFromWishlistBook(
            @PathVariable @Min(value = 1) Long wishlistBookId) {
        bookLogService.deleteBookFromWishlistBook(wishlistBookId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.emptySuccess("찜한 책 삭제 성공"));
    }

    @GetMapping("/users/me/reviews")
    public ResponseEntity<BaseResponseDto<ReviewsResponseDto<CurrentUserReviewDto>>> getCurrentUserReviews(
            @RequestParam(defaultValue = "1") @Min(value = 1) int page,
            @RequestParam(defaultValue = "12") @Min(value = 1) int size) {
        Member member = memberService.getCurrentMember();
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(
                bookLogService.getUserReviewsById(member.getId(), pageable)
        );
    }

    @PostMapping("/reviews")
    public ResponseEntity<BaseResponseDto<CreateReviewResponseDto>>
    createReview(@RequestBody @Valid CreateReviewRequestDto request) {
        CreateReviewResponseDto response = bookLogService.createReview(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                BaseResponseDto.success(response, new BaseResponseDto.Meta("리뷰 생성 성공"))
        );
    }

    @GetMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponseDto<ReviewDetailsResponseDto>>
    getReviewDetails(@PathVariable @Min(value = 1) Long reviewId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.success(bookLogService.getReviewDetails(reviewId),
                        new BaseResponseDto.Meta("조회 성공"))
        );
    }

    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponseDto<Void>>
    updateReview(@PathVariable @Min(value = 1) Long reviewId,
                 @RequestBody @Valid UpdateReviewRequestDto request) {
        bookLogService.updateReview(reviewId, request);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.emptySuccess("리뷰 수정 성공")
        );
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteReview(@PathVariable @Min(value = 1) Long reviewId) {
        bookLogService.deleteReview(reviewId);
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.emptySuccess("리뷰 삭제 성공")
        );
    }
}
