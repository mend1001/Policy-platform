package com.segurosbolivar.polizas.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ErrorResponse {

    private String error;

    public static ErrorResponse of(String message) {
        return ErrorResponse.builder()
                .error(message)
                .build();
    }
}
