package com.beb.backend.domain;

import com.beb.backend.common.ValidationRegexConstants;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "book")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Book {
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

    @NotNull
    @Pattern(regexp = ValidationRegexConstants.ISBN_REGEX)
    @Column(name = "isbn", unique = true, nullable = false)
    private String isbn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Min(value = 0) @Max(value = 5)
    @Column(name = "average_rating")
    private Double averageRating;   // 타입 확인

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @NotNull
    @Column(name = "review_count", nullable = false)
    private int reviewCount;

    @Builder
    public Book(String title, String author, String coverImgUrl,
            String publisher, LocalDate publishedDate,
            String isbn, Category category) {
        this.title = title;
        this.author = author;
        this.coverImgUrl = coverImgUrl;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.category = category;

        this.updatedAt = LocalDateTime.now();
        this.reviewCount = 0;
    }

    public void updateBookInfo(String title, String author, String coverImgUrl,
                               String publisher, LocalDate publishedDate,
                               String isbn, Category category) {
        this.title = title;
        this.author = author;
        this.coverImgUrl = coverImgUrl;
        this.publisher = publisher;
        this.publishedDate = publishedDate;
        this.isbn = isbn;
        this.category = category;

        this.updatedAt = LocalDateTime.now();
    }

    public void addRating(int rating) {
        if (this.reviewCount > 0) {
            this.averageRating = (this.averageRating * this.reviewCount + rating) / (this.reviewCount + 1);
        } else {
            this.averageRating = (double) rating;
        }
        this.reviewCount++;
    }

    public void updateRating(int oldRating, int newRating) {
        if (this.reviewCount == 0) throw new IllegalStateException("평점을 업데이트할 리뷰가 존재하지 않음");
        this.averageRating += ((double) (newRating - oldRating)) / this.reviewCount;
    }

    public void removeRating(int rating) {
        if (this.reviewCount > 1) {
            this.averageRating = (this.averageRating * this.reviewCount - rating) / (this.reviewCount - 1);
        } else {
            this.averageRating = null;
        }
        this.reviewCount--;
    }

    public BigDecimal getAverageRatingAsBigDecimal() {
        return averageRating == null ? null :
                BigDecimal.valueOf(averageRating).setScale(1, RoundingMode.HALF_UP);
    }
}