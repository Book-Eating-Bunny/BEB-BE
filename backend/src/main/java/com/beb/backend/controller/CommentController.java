package com.beb.backend.controller;

import com.beb.backend.dto.BaseResponseDto;
import com.beb.backend.dto.CommentContentDto;
import com.beb.backend.dto.CreatedCommentDto;
import com.beb.backend.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
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
