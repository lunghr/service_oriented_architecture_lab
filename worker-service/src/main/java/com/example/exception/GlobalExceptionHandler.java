package com.example.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .path(request.getDescription(false).replace("uri=", ""))
                        .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .timestamp(ZonedDateTime.now(java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .message(ex.getMessage())
                        .build()
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundRequest(NotFoundException ex, WebRequest request) {
        return ResponseEntity.badRequest().body(
                ErrorResponse.builder()
                        .path(request.getDescription(false).replace("uri=", ""))
                        .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                        .status(HttpStatus.NOT_FOUND.value())
                        .timestamp(ZonedDateTime.now(java.time.ZoneOffset.UTC).format(java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                        .message(ex.getMessage())
                        .build()
        );
    }
}
