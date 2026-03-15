package com.TeamDeadlock.ExploreBangladesh.auth.dto;

import java.time.Instant;

public record ApiError(
        int status,
        String title,
        String message,
        String path,
        boolean error,
        Instant timestamp
) {
    public static ApiError of(int status, String title, String message, String path, boolean error) {
        return new ApiError(status, title, message, path, error, Instant.now());
    }
}
