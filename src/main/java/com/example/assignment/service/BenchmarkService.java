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

import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class BenchmarkService {

    private static final Logger logger = LoggerFactory.getLogger(BenchmarkService.class);
    private static final String SQL_INSERT = "INSERT INTO benchmark (id, unique_code) VALUES (?, ?)";
    private static final Set<String> CACHE = Collections.newSetFromMap(new ConcurrentHashMap<>());
    public static final String SQL_MAX_ROW_ID = "select max(id) from public.benchmark";

    public static AtomicLong MAX_ROW_ID = new AtomicLong(0);

    @Autowired
    BenchmarkRepository repository;

    @Autowired
    HikariDataSource hikariDataSource;

    public void create(long counts) {
        AtomicLong atomicLong = new AtomicLong(MAX_ROW_ID.get());

        MAX_ROW_ID.getAndAdd(counts);

        List<Benchmark> benchmarks = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<?> future = executorService.submit(() -> {
            while (atomicLong.get() < MAX_ROW_ID.get()) {
                String randomAlphanumeric = RandomStringUtils.randomAlphanumeric(7);
                if(CACHE.add(randomAlphanumeric))
                    synchronized(BenchmarkService.class) {
                        benchmarks.add(new Benchmark(atomicLong.incrementAndGet(), randomAlphanumeric));
                    }
                randomAlphanumeric = null;
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
        batchesOfBenchmarks.parallelStream().forEach(batch -> saveAllJdbcBatch(batch));
    }

    private void saveAllJdbcBatch(List<Benchmark> benchmarks) {
        try {
            Connection connection = getConnection();
            PreparedStatement statement = createPreparedStatement(connection, SQL_INSERT);
            for (Benchmark benchmark : benchmarks) {
                statement.clearParameters();
                statement.setLong(1, benchmark.getId());
                statement.setString(2, benchmark.getUniqueCode());
                statement.addBatch();
            }
            statement.executeBatch();
            statement.clearBatch();
            statement.close();
            connection.close();
        } catch (SQLException e) {
            throw new BenchMarkCustomException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        return hikariDataSource.getConnection();
    }

    private static PreparedStatement createPreparedStatement(Connection connection, String sql) throws SQLException {
        return connection.prepareStatement(sql);
    }

}
