package com.beb.backend.dto.responseDto;

import com.beb.backend.domain.Book;

import java.math.BigDecimal;
import java.time.LocalDate;

public record BookDetailsDto(
        Long bookId,
        String coverImgUrl,
        String title,
        String author,
        String publisher,
        LocalDate publishedDate,
        String isbn,
        Integer reviewCount,
        BigDecimal averageRating
) {

    public static BookDetailsDto fromEntity(Book book) {
        return new BookDetailsDto(
                book.getId(),
                book.getCoverImgUrl(),
                book.getTitle(),
                book.getAuthor(),
                book.getPublisher(),
                book.getPublishedDate(),
                book.getIsbn(),
                book.getReviewCount(),
                book.getAverageRatingAsBigDecimal()
        );
    }
}
