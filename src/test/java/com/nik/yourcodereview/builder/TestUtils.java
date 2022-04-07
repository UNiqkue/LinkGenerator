package com.nik.yourcodereview.builder;

import org.springframework.http.HttpHeaders;

public class TestUtils {
    public static HttpHeaders buildHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        httpHeaders.add("Accept", "application/json");
        return httpHeaders;
    }
}
