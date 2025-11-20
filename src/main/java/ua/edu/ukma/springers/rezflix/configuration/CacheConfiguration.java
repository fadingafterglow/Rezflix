package ua.edu.ukma.springers.rezflix.configuration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ua.edu.ukma.springers.rezflix.caching.CustomCacheManager;

@Configuration
@EnableCaching
public class CacheConfiguration {
    @Bean
    public CacheManager cacheManager() {
        return new CustomCacheManager();
    }
}
