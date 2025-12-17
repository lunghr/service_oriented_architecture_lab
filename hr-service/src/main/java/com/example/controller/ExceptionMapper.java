package com.example.controller;


import com.example.model.ErrorResponse;
import com.example.model.NotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionMapper {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFoundException(NotFoundException ex) {
        return ResponseEntity
                .status(404)
                .body(ErrorResponse.builder()
                        .code(404)
                        .message(ex.getMessage())
                        .build()
                );
    }
}
