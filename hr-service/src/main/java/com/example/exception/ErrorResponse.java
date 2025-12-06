package com.example.exception;
import lombok.*;

@Builder
public record ErrorResponse (int status, String message) {
}