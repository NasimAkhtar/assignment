package com.example.assignment.controller;

import com.example.assignment.service.BenchmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
@RestController
public class BenchmarkController {

    @Autowired
    private BenchmarkService benchmarkService;

    @PostMapping("/")
    public ResponseEntity<String> create(@RequestBody int counts) {
        benchmarkService.create(counts);
        return ResponseEntity.ok("Records Created");
    }

}
