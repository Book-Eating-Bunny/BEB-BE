package com.beb.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "book")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
    // TODO: updatedAt 추가해서 갱신 날짜 저장
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @NotNull
    @Column(name = "title", nullable = false)
    private String title;

    @NotNull
    @Column(name = "author", nullable = false)
    private String author;

    @NotNull    // 받아온 정보 없을 시 기본 이미지
    @Column(name = "cover_img_url", nullable = false)
    private String coverImgUrl;

    @Column(name = "publisher")
    private String publisher;

    @Column(name = "published_date")
    private LocalDate publishedDate;

    @Column(name = "isbn", unique = true)
    private String isbn;

    @Column(name = "category")
    private String category;

    @Min(value = 0) @Max(value = 5)
    @Column(name = "average_rating")
    private Double averageRating;   // 타입 확인

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Builder
    public Book(String title, String author, String coverImgUrl,
            String publisher, LocalDate publishedDate,
            String isbn, String category) {
        this.title = title;
        this.author = author;
        this.coverImgUrl = coverImgUrl;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.category = category;
        this.reviewCount = 0;
    }
}