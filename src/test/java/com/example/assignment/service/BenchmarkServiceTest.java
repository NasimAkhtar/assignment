package com.example.assignment.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class BenchmarkServiceTest {

    @Autowired
    BenchmarkService benchmarkService;
    @Test
    @Timeout(value = 350, unit = TimeUnit.MILLISECONDS)
    void testCreateFor50KRecords() {
        benchmarkService.create(50000);
    }

    @Test
    @Timeout(value = 650, unit = TimeUnit.MILLISECONDS)
    void testCreateFor100KRecords() {
        benchmarkService.create(100000);
    }

    @Test
    @Timeout(value = 850, unit = TimeUnit.MILLISECONDS)
    void testCreateFor150KRecords() {
        benchmarkService.create(150000);
    }
}