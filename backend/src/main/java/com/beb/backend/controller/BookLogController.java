package com.beb.backend.controller;

import com.beb.backend.dto.AddWishlistBookRequestDto;
import com.beb.backend.dto.BaseResponseDto;
import com.beb.backend.dto.AddReadBookRequestDto;
import com.beb.backend.service.BookLogService;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users/me")
public class BookLogController {
    private final BookLogService bookLogService;

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
}
