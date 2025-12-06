package com.example.exception;
import lombok.Builder;

@Builder
public record ErrorResponse(String timestamp, int status, String error, String message, String path) {
}
