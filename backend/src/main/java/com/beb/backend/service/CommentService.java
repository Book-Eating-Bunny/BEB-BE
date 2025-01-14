package com.beb.backend.service;

import com.beb.backend.domain.Comment;
import com.beb.backend.domain.Member;
import com.beb.backend.dto.CommentContentDto;
import com.beb.backend.dto.CreatedCommentDto;
import com.beb.backend.exception.BookLogException;
import com.beb.backend.exception.BookLogExceptionInfo;
import com.beb.backend.exception.MemberException;
import com.beb.backend.exception.MemberExceptionInfo;
import com.beb.backend.repository.CommentRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final MemberService memberService;
    private final CommentRepository commentRepository;

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
