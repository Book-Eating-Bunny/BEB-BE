package com.beb.backend.dto.responseDto;

import java.util.List;

public record CommentListDto<T>(
        List<T> comments
) {
}
