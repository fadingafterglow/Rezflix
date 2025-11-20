package ua.edu.ukma.springers.rezflix.caching;

import org.springframework.cache.support.AbstractValueAdaptingCache;
import org.springframework.lang.NonNull;

import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class CustomCache extends AbstractValueAdaptingCache {
    private final String name;
    private final ConcurrentHashMap<Object, Object> cacheMap = new ConcurrentHashMap<>();

    protected CustomCache(String name) {
        super(false);
        this.name = name;
    }

    @Override
    protected Object lookup(@NonNull Object key) {
        return cacheMap.get(key);
    }

    @Override
    @NonNull
    public String getName() {
        return name;
    }

    @Override
    @NonNull
    public Object getNativeCache() {
        return cacheMap;
    }

    @Override
    public <T> T get(@NonNull Object key, @NonNull Callable<T> valueLoader) {
        ValueWrapper wrapper = get(key);
        if(wrapper != null) {
            return (T) wrapper.get();
        }
        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception ex) {
            throw new ValueRetrievalException(key, valueLoader, ex);
        }
    }

    @Override
    public void put(@NonNull Object key, Object value) {
        cacheMap.put(key, value);
    }

    @Override
    public void evict(@NonNull Object key) {
        cacheMap.remove(key);
    }

    @Override
    public void clear() {
        cacheMap.clear();
    }
}
