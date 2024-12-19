package com.beb.backend.repository;

import com.beb.backend.domain.Book;
import com.beb.backend.domain.Comment;
import com.beb.backend.domain.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    // 특정 Member가 작성한 리뷰 조회
    @Query("SELECT c from Comment c WHERE c.member.id = :memberId AND c.parentComment IS NULL")
    Page<Comment> findReviewsByMemberId(@Param("memberId") Long memberId, Pageable pageable);

    // 특정 Book에 대한 리뷰 조회 (페이징 적용)
    @Query("SELECT c FROM Comment c WHERE c.book.id = :bookId AND c.parentComment IS NULL")
    Page<Comment> findReviewsByBookId(@Param("bookId") Long bookId, Pageable pageable);

    // 특정 리뷰에 달린 댓글 조회
    @Query("SELECT c FROM Comment c WHERE c.parentComment.id = :reviewId")
    List<Comment> findCommentsByParentReviewId(@Param("reviewId") Long reviewId);

    boolean existsByMemberAndBookAndParentCommentIsNull(Member member, Book book);
}
