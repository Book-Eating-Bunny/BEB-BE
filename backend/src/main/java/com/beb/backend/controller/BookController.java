package com.beb.backend.controller;

import com.beb.backend.dto.BaseResponseDto;
import com.beb.backend.dto.BooksResponseDto;
import com.beb.backend.dto.SearchBookInfoDto;
import com.beb.backend.service.BookService;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
}
