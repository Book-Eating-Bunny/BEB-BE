package com.beb.backend.controller;

import com.beb.backend.common.ValidationRegexConstants;
import com.beb.backend.dto.responseDto.BaseResponseDto;
import com.beb.backend.dto.responseDto.BookAndUserStatusDto;
import com.beb.backend.dto.responseDto.BookListDto;
import com.beb.backend.dto.responseDto.SearchBookInfoDto;
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
    public ResponseEntity<BaseResponseDto<BookListDto<SearchBookInfoDto>>>
    searchBooksByNaverApi(@RequestParam @NotBlank String query,
                          @RequestParam(required = false, defaultValue = "1") @Min(value = 1) int page,
                          @RequestParam(required = false, defaultValue = "30") @Min(value = 1) int size) {

        return ResponseEntity.status(HttpStatus.OK)
                .body(bookService.searchBooksByNaverApi(query, page, size));
    }

    /**
     * ISBN 으로 도서 상세 정보 조회. DB에서 먼저 찾고, DB에 없을 경우는 알라딘 API를 호출하여 도서 정보를 받아와 DB에 저장 후 반환.
     * GET 메서드이지만 DB에 쓰는 작업이 수행될 수 있다.
     * 알라딘을 통한 검색 결과를 얻지 못했을 경우에 404 NOT FOUND 응답을 반환한다.
     * @param isbn 도서 ISBN (13자리)
     * @return isbn으로 찾은 도서 상세 정보와 현재 사용자의 책에 대한 상태(읽은 책인지, 찜한 책인지, 리뷰를 작성했는지) 정보
     */
    @GetMapping("/isbn/{isbn}")
    public ResponseEntity<BaseResponseDto<BookAndUserStatusDto>>
    getBookDetailsByIsbn(@PathVariable @Pattern(regexp = ValidationRegexConstants.ISBN_REGEX) String isbn) {
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.ofSuccess(bookService.getBookDetailsByIsbn(isbn), "조회 성공")
        );
    }

    /**
     * bookId 로 도서 상세 정보 조회. DB에 없을 경우 바로 404 NOT FOUND 응답을 반환한다.
     * @param bookId 도서 PK
     * @return bookId로 찾은 도서 상세 정보와 현재 사용자의 책에 대한 상태(읽은 책인지, 찜한 책인지, 리뷰를 작성했는지) 정보
     */
    @GetMapping("/{bookId}")
    public ResponseEntity<BaseResponseDto<BookAndUserStatusDto>>
    getBookDetailsById(@PathVariable @Min(value = 1) Long bookId) {
        return ResponseEntity.status(HttpStatus.OK).body(
                BaseResponseDto.ofSuccess(bookService.getBookDetailsById(bookId), "조회 성공")
        );
    }
}
