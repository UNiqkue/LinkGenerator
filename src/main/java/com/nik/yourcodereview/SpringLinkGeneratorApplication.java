package com.nik.yourcodereview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class SpringLinkGeneratorApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringLinkGeneratorApplication.class, args);
    }
}
