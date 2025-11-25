package ua.edu.ukma.springers.rezflix.caching;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.lang.NonNull;

import java.util.Collection;
import java.util.List;

public class CustomCacheManager extends AbstractCacheManager {

    @Value("${spring.cache.cache-names:}")
    private List<String> cacheNames;

    @Override
    @NonNull
    protected Collection<? extends Cache> loadCaches() {
        return cacheNames.stream()
                .map(CustomCache::new)
                .toList();
    }
}
