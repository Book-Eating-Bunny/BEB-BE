package com.beb.backend.dto.responseDto;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * 기본 Response DTO. "result", "data", "meta"를 key로 갖는 JSON Response Body를 전달하기 위한 DTO
 */
@JsonInclude(JsonInclude.Include.NON_NULL)  // null 값은 JSON에 포함되지 않음
public record BaseResponseDto<T>(
        Integer result,
        T data,
        Meta meta) {

    public static <T> BaseResponseDto<T> ofSuccess(T data, String message) {
        return new BaseResponseDto<>(1, data, Meta.ofMessage(message));
    }

    public static <T> BaseResponseDto<T> ofFailure(String message) {
        return new BaseResponseDto<>(0, null, Meta.ofMessage(message));
    }

    public static BaseResponseDto<Void> ofEmptySuccess(String message) {
        return new BaseResponseDto<>(1, null, Meta.ofMessage(message));
    }

    public static <T> BaseResponseDto<T> ofSuccessWithPagination(T data, String message, int pageNumber, int totalPages, long totalElements) {
        return new BaseResponseDto<>(1, data, Meta.ofPagination(message, pageNumber, totalPages, totalElements));
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Meta(
            String message,
            Integer currentPage,
            Integer totalPages,
            Long totalElements) {

        // 메시지만 포함된 메타 정보
        public static Meta ofMessage(String message) {
            return new Meta(message, null, null, null);
        }

        // 페이징 정보 포함한 메타 정보
        public static Meta ofPagination(String message, int pageNumber, int totalPages, long totalElements) {
            return new Meta(message, pageNumber + 1, totalPages, totalElements);
        }
    }
}
