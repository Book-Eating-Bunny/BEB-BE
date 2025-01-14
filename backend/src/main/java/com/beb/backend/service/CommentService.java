package com.beb.backend.service;

import com.beb.backend.domain.Comment;
import com.beb.backend.domain.Member;
import com.beb.backend.dto.*;
import com.beb.backend.exception.BookLogException;
import com.beb.backend.exception.BookLogExceptionInfo;
import com.beb.backend.exception.MemberException;
import com.beb.backend.exception.MemberExceptionInfo;
import com.beb.backend.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberService memberService;
    private final CommentRepository commentRepository;

    private CommentDetailsDto mapToCommentDetailsDto(Comment comment) {
        return new CommentDetailsDto(
                comment.getId(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.getUpdatedAt(),
                new MemberSummaryDto(
                        comment.getMember().getId(),
                        comment.getMember().getNickname()
                )
        );
    }

    public BaseResponseDto<CommentsResponseDto<CommentDetailsDto>>
    getAllCommentsAboutReview(Long reviewId, Pageable pageable) {
        if (!commentRepository.existsReviewById(reviewId)) {
            throw new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND);
        }
        Page<Comment> commentsPage = commentRepository.findCommentsByParentReviewId(reviewId, pageable);
        List<CommentDetailsDto> comments = commentsPage.getContent().stream()
                .map(this::mapToCommentDetailsDto).toList();

        BaseResponseDto.Meta meta = BaseResponseDto.Meta.createPaginationMeta(
                commentsPage.getNumber(), commentsPage.getTotalPages(), commentsPage.getTotalElements(),
                "조회 성공"
        );
        return BaseResponseDto.success(new CommentsResponseDto<>(comments), meta);
    }

    @Transactional
    public CreatedCommentDto createComment(Long reviewId, CommentContentDto request) {
        Comment review = commentRepository.findReviewById(reviewId)
                .orElseThrow(() -> new BookLogException(BookLogExceptionInfo.REVIEW_NOT_FOUND));

        Member member = memberService.getCurrentMember()
                .orElseThrow(() -> new MemberException(MemberExceptionInfo.MEMBER_NOT_FOUND));

        Comment savedComment = commentRepository.save(Comment.createComment(review, member, request.content()));
        return new CreatedCommentDto(savedComment.getId());
    }
}
