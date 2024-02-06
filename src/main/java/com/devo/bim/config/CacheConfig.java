package com.devo.bim.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
@EnableCaching
public class CacheConfig implements WebMvcConfigurer {
    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(
                new ConcurrentMapCache("menus"),
                new ConcurrentMapCache("config"),
                new ConcurrentMapCache("weatherLocation"),
                new ConcurrentMapCache("projectCustomConfig"),
                new ConcurrentMapCache("productProvider"),
                new ConcurrentMapCache("wbsCode")
                ));
        return cacheManager;
    }
}