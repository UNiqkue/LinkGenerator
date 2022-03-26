package com.nik.yourcodereview.configuration;

import org.hashids.Hashids;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class HashidsConfiguration {

    @Bean("HashGenerator")
    public Function<String, Hashids> hashGenerator() {
        return Hashids::new;
    }
}
