package com.example.assignment.repository;

import com.example.assignment.model.Benchmark;
import org.springframework.data.repository.CrudRepository;

public interface BenchmarkRepository extends CrudRepository<Benchmark, Long> {
}