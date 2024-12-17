package com.beb.backend.dto;

import java.math.BigDecimal;

public record BookSummaryWithRatingDto(Long bookId, String coverImgUrl,
                                       String title, String author, BigDecimal averageRating) {
}
