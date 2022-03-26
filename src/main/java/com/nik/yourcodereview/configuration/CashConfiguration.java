package com.nik.yourcodereview.configuration;


import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class CashConfiguration {

    @Bean
    @Primary
    public CacheManager links() {
        return new ConcurrentMapCacheManager();
    }

    @Bean
    public CacheManager generateLinks() {
        return new ConcurrentMapCacheManager();
    }
}
