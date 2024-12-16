package com.beb.backend.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "comment")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Comment parentComment;      // 리뷰면 null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id")
    private Book book;                  // 댓글이면 null

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @NotNull
    @Lob
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "rating")
    private Integer rating;             // 댓글이면 null

    @Column(name = "is_spoiler")
    private Boolean isSpoiler;          // 댓글이면 null

    @Column(name = "is_public")
    private Boolean isPublic;           // 댓글이면 null

    @Column(name = "like_count")
    private Integer likeCount;          // 댓글이면 null

    @NotNull
    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @NotNull
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Comment(Comment parentComment, Book book, Member member,
                   String content, Integer rating, Boolean isSpoiler, Boolean isPublic, Integer likeCount) {
        this.parentComment = parentComment;
        this.book = book;
        this.member = member;
        this.content = content;
        this.rating = rating;
        this.isSpoiler = isSpoiler;
        this.isPublic = isPublic;
        this.likeCount = likeCount;

        this.updatedAt = LocalDateTime.now();
    }

    public static Comment createReview(Book book, Member member,
                                       String content, Integer rating, Boolean isSpoiler, Boolean isPublic) {
        return new Comment(null, book, member, content, rating, isSpoiler, isPublic, 0);
    }

    public static Comment createComment(Comment review, Member member, String content) {
        return new Comment(review, null, member, content, null, null, null, null);
    }

    public void incrementLikeCount() {
        this.likeCount++;
    }

    public void decrementLikeCount() {
        this.likeCount--;
    }
}
