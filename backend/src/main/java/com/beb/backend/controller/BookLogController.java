package com.beb.backend.controller;

import com.beb.backend.domain.Member;
import com.beb.backend.dto.*;
import com.beb.backend.service.BookLogService;
import com.beb.backend.service.MemberService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me")
public class BookLogController {
    private final BookLogService bookLogService;
    private final MemberService memberService;

    @PostMapping("/read-books")
    public ResponseEntity<BaseResponseDto<Void>> addBookToReadBook(@RequestBody AddReadBookRequestDto request) {
        bookLogService.addBookToReadBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.emptySuccess("읽은 책 추가 성공"));
    }

    @DeleteMapping("/read-books/{readBookId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteBookFromReadBook(@PathVariable @Min(value = 1) Long readBookId) {
        bookLogService.deleteBookFromReadBook(readBookId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.emptySuccess("읽은 책 삭제 성공"));
    }

    @PostMapping("/want-to-read-books")
    public ResponseEntity<BaseResponseDto<Void>> addBookToWishlistBook(@RequestBody AddWishlistBookRequestDto request) {
        bookLogService.addBookToWishlistBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(BaseResponseDto.emptySuccess("찜한 책 추가 성공"));
    }

    @DeleteMapping("/want-to-read-books/{wishlistBookId}")
    public ResponseEntity<BaseResponseDto<Void>> deleteBookFromWishlistBook(
            @PathVariable @Min(value = 1) Long wishlistBookId) {
        bookLogService.deleteBookFromWishlistBook(wishlistBookId);
        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.emptySuccess("찜한 책 삭제 성공"));
    }

    @GetMapping("/reviews")
    public ResponseEntity<BaseResponseDto<ReviewsResponseDto<CurrentUserReviewDto>>> getCurrentUserReviews(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "12") int size) {
        Member member = memberService.getCurrentMember();

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ResponseEntity.status(HttpStatus.OK).body(
                bookLogService.getUserReviewsById(member.getId(), pageable)
        );
    }
}
