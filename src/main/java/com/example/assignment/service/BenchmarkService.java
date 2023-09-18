package com.example.assignment.service;

import com.example.assignment.exception.BenchMarkCustomException;
import com.example.assignment.model.Benchmark;
import com.example.assignment.repository.BenchmarkRepository;
import com.example.assignment.util.ListUtil;
import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.RandomStringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BenchmarkService {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkService.class);
    private static final String SQL_INSERT = "INSERT INTO benchmark (id, unique_code) VALUES (?, ?)";
    @Autowired
    BenchmarkRepository repository;

    @Autowired
    HikariDataSource hikariDataSource;

    public void create(int counts) {
        AtomicLong atomicLong = new AtomicLong(0);

        List<Benchmark> benchmarks = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<?> future = executorService.submit(() -> {
            while (atomicLong.getAndIncrement() < counts) {
                benchmarks.add(new Benchmark(atomicLong.get(),
                        RandomStringUtils.randomAlphanumeric(7)));
            }
        });

        while (!future.isDone()) {
            try {
                Thread.sleep(3);
            } catch (InterruptedException e) {
                throw new BenchMarkCustomException(e);
            }
        }
        saveAll(benchmarks);
    }

    private void saveUsingJPA(List<Benchmark> benchmarks) {
        repository.saveAll(benchmarks);
    }


    private void saveAll(List<Benchmark> benchmarks) {
        List<List<Benchmark>> batchesOfBenchmarks = ListUtil.createSubList(benchmarks, 5000);
        try {
            Connection connection = hikariDataSource.getConnection();
            Statement statement = connection.createStatement();
            statement.execute("truncate table public.benchmark");
        } catch (SQLException e) {
            throw new BenchMarkCustomException(e);
        }
        batchesOfBenchmarks.parallelStream().forEach(batch -> saveAllJdbcBatch(batch));
    }

    private void saveAllJdbcBatch(List<Benchmark> benchmarks) {
        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(SQL_INSERT)) {
            for (Benchmark benchmark : benchmarks) {
                statement.clearParameters();
                statement.setLong(1, benchmark.getId());
                statement.setString(2, benchmark.getUniqueCode());
                statement.addBatch();
            }
            statement.executeBatch();
            statement.clearBatch();
        } catch (SQLException e) {
            throw new BenchMarkCustomException(e);
        }
    }

}
