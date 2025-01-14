package com.beb.backend.controller;

import com.beb.backend.dto.*;
import com.beb.backend.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
@Validated
public class CommentController {

    private final CommentService commentService;

    @GetMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<BaseResponseDto<CommentsResponseDto<CommentDetailsDto>>>
    getAllCommentsAboutReview(@PathVariable @Min(value = 1) Long reviewId,
                              @RequestParam(defaultValue = "1") @Min(value = 1) int page,
                              @RequestParam(defaultValue = "50") @Min(value = 1) int size) {
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt"));
        return ResponseEntity.status(HttpStatus.OK).body(
                commentService.getAllCommentsAboutReview(reviewId, pageable)
        );
    }

    @PostMapping("/reviews/{reviewId}/comments")
    public ResponseEntity<BaseResponseDto<CreatedCommentDto>>
    createComment(@PathVariable @Min(value = 1) Long reviewId,
                  @RequestBody @Valid CommentContentDto request) {

        return ResponseEntity.status(HttpStatus.OK).body(BaseResponseDto.success(
                commentService.createComment(reviewId, request),
                new BaseResponseDto.Meta("댓글 생성 성공"))
        );
    }
}