package com.procurement.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {
    // Cache configuration is handled by Spring Boot auto-configuration
    // with Redis as the cache provider
}
