package com.segurosbolivar.polizas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private int httpStatus;
    private LocalDateTime timestamp;
    private String message;
    private T data;

    public static <T> ApiResponse<T> ok(T data, String message) {
        return ApiResponse.<T>builder()
                .httpStatus(HttpStatus.OK.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> created(T data, String message) {
        return ApiResponse.<T>builder()
                .httpStatus(HttpStatus.CREATED.value())
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(data)
                .build();
    }

    public static ApiResponse<Void> error(int status, String message) {
        return ApiResponse.<Void>builder()
                .httpStatus(status)
                .timestamp(LocalDateTime.now())
                .message(message)
                .data(null)
                .build();
    }
}
