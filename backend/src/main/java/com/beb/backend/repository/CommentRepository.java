package com.beb.backend.repository;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Comment;
import com.beb.backend.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @Query("SELECT c FROM Comment c WHERE c.id = :reviewId AND c.parentComment IS NULL")
    Optional<Comment> findReviewById(Long reviewId);

    // 특정 Member가 작성한 리뷰 조회
    @Query("SELECT c FROM Comment c WHERE c.member = :member AND c.parentComment IS NULL")
    Page<Comment> findReviewsByMember(@Param("member") Member member, Pageable pageable);

    @Query("SELECT c FROM Comment c WHERE c.member = :member AND c.parentComment IS NULL AND c.isPublic IS TRUE ")
    Page<Comment> findPublicReviewsByMember(@Param("member") Member member, Pageable pageable);

    // 특정 Book에 대한 리뷰 조회 (페이징 적용)
    @Query("SELECT c FROM Comment c WHERE c.book.id = :bookId AND c.parentComment IS NULL AND c.isPublic IS TRUE")
    Page<Comment> findPublicReviewsByBookId(@Param("bookId") Long bookId, Pageable pageable);

    @Query("SELECT c FROM Comment c " +
            "WHERE c.book.id = :bookId AND c.parentComment IS NULL AND (c.isPublic IS TRUE OR c.member.id = :memberId)")
    Page<Comment> findVisibleReviewsByBookIdAndMemberId(@Param("bookId") Long bookId, @Param("memberId") Long memberId, Pageable pageable);

    // 공개된 전체 리뷰 조회
    @Query("SELECT c FROM Comment c WHERE c.parentComment IS NULL AND c.isPublic IS TRUE")
    Page<Comment> findAllPublicReviews(Pageable pageable);

    // 해당 사용자가 조회 가능한 전체 리뷰 조회
    @Query("SELECT c FROM Comment c " +
            "WHERE c.parentComment IS NULL AND (c.isPublic IS TRUE OR c.member.id = :memberId)")
    Page<Comment> findAllVisibleReviewsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 특정 리뷰에 달린 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :reviewId")
    Page<Comment> findCommentsByParentReviewId(@Param("reviewId") Long reviewId, Pageable pageable);

    Optional<Comment> findByMemberAndBookAndParentCommentIsNull(Member member, Book book);

    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.id = :reviewId AND c.parentComment IS NULL")
    boolean existsReviewById(Long reviewId);

    boolean existsByMemberAndBookAndParentCommentIsNull(Member member, Book book);
}
