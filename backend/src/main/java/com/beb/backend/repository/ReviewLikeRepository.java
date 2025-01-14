package com.beb.backend.repository;

import com.beb.backend.domain.ReviewLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReviewLikeRepository extends JpaRepository<ReviewLike, Long> {
    Optional<ReviewLike> findById(long id);

    @Query("SELECT rl FROM ReviewLike rl WHERE rl.member.id = :memberId AND rl.comment.id = :reviewId")
    Optional<ReviewLike> findByMemberIdAndReviewId(@Param("memberId") Long memberId, @Param("reviewId") Long reviewId);

    @Query("SELECT COUNT(rl) > 0 FROM ReviewLike rl WHERE rl.member.id = :memberId AND rl.comment.id = :reviewId")
    boolean existsByMemberIdAndCommentId(@Param("memberId") Long memberId, @Param("reviewId") Long reviewId);
}
