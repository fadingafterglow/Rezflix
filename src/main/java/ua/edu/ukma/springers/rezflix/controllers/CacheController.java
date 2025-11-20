package ua.edu.ukma.springers.rezflix.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ua.edu.ukma.springers.rezflix.controllers.rest.api.CacheControllerApi;
import ua.edu.ukma.springers.rezflix.exceptions.NotFoundException;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CacheController implements CacheControllerApi {
    private final CacheManager cacheManager;

    @Override
    public ResponseEntity<Void> clearCache(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if(cache == null) {
            throw new NotFoundException();
        }
        cache.clear();
        log.info("Cache {} was cleared", cacheName);
        return ResponseEntity.noContent().build();
    }
}
