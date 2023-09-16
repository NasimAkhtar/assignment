package com.example.assignment.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.net.URL;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class BenchmarkControllerTest {
    public static final String URL = "http://localhost:";
    public static final String FORWARD_SLASH = "/";
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @Timeout(value = 650, unit = TimeUnit.MILLISECONDS)
    public void testCreateFor50KRecords() throws Exception {
        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        new URL(URL + port + FORWARD_SLASH).toString(),
                        50000,
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Records Created", response.getBody());
    }
    @Test
    @Timeout(value = 850, unit = TimeUnit.MILLISECONDS)
    public void testCreateFor100KRecords() throws Exception {
        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        new URL(URL + port + FORWARD_SLASH).toString(),
                        100000,
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Records Created", response.getBody());
    }
    @Test
    @Timeout(value = 1000, unit = TimeUnit.MILLISECONDS)
    public void testCreateFor150KRecords() throws Exception {
        ResponseEntity<String> response = restTemplate
                .postForEntity(
                        new URL(URL + port + FORWARD_SLASH).toString(),
                        150000,
                        String.class
                );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("Records Created", response.getBody());
    }
}