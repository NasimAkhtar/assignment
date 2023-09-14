package com.example.assignment;

import com.zaxxer.hikari.HikariDataSource;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
@RestController
public class BenchmarkController {
    @Autowired
    BenchmarkRepository repository;

    @Autowired
    HikariDataSource hikariDataSource;
    @GetMapping("/")
    public String home() {
        return "Hello World!!";
    }

    @PostMapping("/")
    public long create(@RequestBody Long count) throws InterruptedException {
        long l = System.currentTimeMillis();
        AtomicLong counts = new AtomicLong(0);

        List<Benchmark> objects = Collections.synchronizedList(new ArrayList<>());

        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<?> future = executorService.submit(() -> {
            while (counts.getAndIncrement() < count) {
                objects.add(new Benchmark(counts.get(), RandomStringUtils.randomAlphanumeric(7)));
          }
        });

        while (!future.isDone()) {
            Thread.sleep(500);
        }

        //saveAllJdbcBatch(objects);
        saveAllJdbcBatchCallable(objects);

        return  (System.currentTimeMillis()-l)/100;
    }

    public void saveAllJdbcBatch(List<Benchmark> benchmarks){
        System.out.println("insert using jdbc batch");
        String sql = String.format(
                "INSERT INTO benchmark (id, unique_code) VALUES (?, ?)");

        try (Connection connection = hikariDataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)
        ){
            int counter = 0;
            for (Benchmark benchmark : benchmarks) {
                statement.clearParameters();
                statement.setLong(1, benchmark.getId());
                statement.setString(2, benchmark.getUniqueCode());
                statement.addBatch();
                if ((counter + 1) % 1000 == 0 || (counter + 1) == benchmarks.size()) {
                    statement.executeBatch();
                    statement.clearBatch();
                }
                counter++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saveAllJdbcBatchCallable(List<Benchmark> benchmarks){
        System.out.println("insert using jdbc batch, threading");
        System.out.print("cp size " + hikariDataSource.getMaximumPoolSize());
        List<List<Benchmark>> listOfBm = createSubList(benchmarks, 5000);
        ExecutorService executorService = Executors.newFixedThreadPool(hikariDataSource.getMaximumPoolSize());
        List<Callable<Integer>> callables = listOfBm.stream().map(sublist ->
                (Callable<Integer>) () -> {
                    saveAllJdbcBatch(sublist);
                    return sublist.size();
                }).collect(Collectors.toList());
        try {
            List<Future<Integer>> futures = executorService.invokeAll(callables);
            int count = 0;
            for(Future<Integer> future: futures){
                count += future.get();
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static <T> List<List<T>> createSubList(List<T> list, int subListSize){
        List<List<T>> listOfSubList = new ArrayList<>();
        for (int i = 0; i < list.size(); i+=subListSize) {
            if(i + subListSize <= list.size()){
                listOfSubList.add(list.subList(i, i + subListSize));
            }else{
                listOfSubList.add(list.subList(i, list.size()));
            }
        }
        return listOfSubList;
    }
}
