package com.example.assignment.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class BenchMarkControllerAdvice {

    private static final Logger logger = LoggerFactory.getLogger(BenchMarkControllerAdvice.class);

    @ExceptionHandler(BenchMarkCustomException.class)
    public ResponseEntity<Object> handleException(BenchMarkCustomException exception) {
        logger.error(exception.getMessage());
        return ResponseEntity.internalServerError().body(exception.getMessage());
    }
}
