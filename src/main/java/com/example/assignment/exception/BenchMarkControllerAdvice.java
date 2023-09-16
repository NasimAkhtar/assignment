package com.example.assignment.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BenchMarkControllerAdvice {

    @ExceptionHandler(BenchMarkCustomException.class)
    public ResponseEntity<Object> handleException(BenchMarkCustomException exception) {
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }
}
