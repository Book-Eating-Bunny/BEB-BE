package com.beb.backend.controller;

import com.beb.backend.dto.BaseResponseDto;
import com.beb.backend.dto.AddReadBookRequestDto;
import com.beb.backend.service.BookLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
