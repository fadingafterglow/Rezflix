package ua.edu.ukma.springers.rezflix.caching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.lang.NonNull;

import java.util.Arrays;
import java.util.Collection;

public class CustomCacheManager extends AbstractCacheManager {
    @Value("${spring.cache.cache-names}")
    private String cacheNames = "";

    @Override
    @NonNull
    protected Collection<? extends Cache> loadCaches() {
        return Arrays.stream(cacheNames.trim().split(","))
                .map(String::trim)
                .map(CustomCache::new)
                .toList();
    }
}
