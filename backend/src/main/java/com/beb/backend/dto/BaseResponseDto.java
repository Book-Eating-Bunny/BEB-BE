package com.beb.backend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 기본 Response DTO. "result", "data", "meta"를 key로 갖는 JSON Response Body를 전달하기 위한 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 값은 JSON에 포함되지 않음
public record BaseResponseDto<T>(
        Integer result,
        T data,
        Meta meta
) {

    public static <T> BaseResponseDto<T> success(T data, Meta meta) {
        return new BaseResponseDto<>(1, data, meta);
    }

    public static <T> BaseResponseDto<T> fail(String message) {
        return new BaseResponseDto<>(0, null, new Meta(message));
    }

    public static BaseResponseDto<Void> emptySuccess(String message) {
        return new BaseResponseDto<>(1, null, new Meta(message));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Meta(
            String message,
            Integer currentPage,
            Integer totalPages,
            Integer totalElements
    ) {
        public Meta(String message) {
            this(message, null, null, null);
        }
    }
}
