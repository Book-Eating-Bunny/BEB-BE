package com.beb.backend.dto;


import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BookAndUserStatusDto(
        BookDetailsDto book,
        UserStatusDto userStatus
) {
}
