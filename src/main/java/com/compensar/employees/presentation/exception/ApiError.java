package com.compensar.employees.presentation.exception;

import java.time.Instant;

public record ApiError(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path) {
}
