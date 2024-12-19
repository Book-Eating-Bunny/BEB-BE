package com.beb.backend.controller;

import com.beb.backend.common.ValidationRegexConstants;
import com.beb.backend.dto.*;
import com.beb.backend.service.BookService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/books")
@Validated
public class BookController {

    private final BookService bookService;

    @GetMapping
    public ResponseEntity<BaseResponseDto<BooksResponseDto<SearchBookInfoDto>>>
    searchBooksByNaverApi(@RequestParam @NotBlank String query,
                          @RequestParam(required = false, defaultValue = "1") @Min(value = 1) int page,
                          @RequestParam(required = false, defaultValue = "30") @Min(value = 1) int size) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(bookService.searchBooksByNaverApi(query, page, size));
    }

    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BaseResponseDto<BookAndUserStatusDto>>
    getBookDetailsByIsbn(@PathVariable @Pattern(regexp = ValidationRegexConstants.ISBN_REGEX) String isbn) {
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.success(bookService.getBookDetailsByIsbn(isbn), new BaseResponseDto.Meta("조회 성공"))
        );
    }
}
