package com.beb.backend.dto;

/**
 * email, nickname 등의 사용 가능 여부를 담는 DTO
 * @param isAvailable (boolean)
 */
public record AvailabilityResponseDto(
        boolean isAvailable
) { }
