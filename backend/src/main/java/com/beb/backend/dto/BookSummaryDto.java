package com.beb.backend.dto;


public record BookSummaryDto(Long bookId, String coverImgUrl, String title, String author) {
}