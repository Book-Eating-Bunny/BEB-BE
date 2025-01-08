package com.beb.backend.dto;


import com.beb.backend.domain.Book;

public record BookSummaryDto(
        Long bookId,
        String coverImgUrl,
        String title,
        String author) {

    public static BookSummaryDto fromEntity(Book book) {
        return new BookSummaryDto(
                book.getId(),
                book.getCoverImgUrl(),
                book.getTitle(),
                book.getAuthor()
        );
    }
}