package com.example.assignment.exception;

public class BenchMarkCustomException extends RuntimeException {
    public BenchMarkCustomException(Exception e) {
        super(e);
    }
}
