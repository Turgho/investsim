package com.turgho.investsim.web;

import java.time.LocalDateTime;
import java.util.List;

public record ApiErrorResponse(
    LocalDateTime timestamp,
    int status,
    List<String> errors,
    String path
) {
    public static ApiErrorResponse of(int status, String error, String path) {
        return new ApiErrorResponse(LocalDateTime.now(), status, List.of(error), path);
    }

    public static ApiErrorResponse of(int status, List<String> errors, String path) {
        return new ApiErrorResponse(LocalDateTime.now(), status, errors, path);
    }
}
