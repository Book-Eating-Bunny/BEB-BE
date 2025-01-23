package com.beb.backend.dto.responseDto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookAndUserStatusDto(
        BookDetailsDto book,
        UserStatusDto userStatus
) {
}
