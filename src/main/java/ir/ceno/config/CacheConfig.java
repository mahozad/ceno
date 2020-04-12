package ir.ceno.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import ir.ceno.model.Post;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.github.benmanes.caffeine.cache.Caffeine.newBuilder;

/**
 * Configuration for creating and managing caches.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Creates caffeine caches for {@link Post}s or just files.
     *
     * @return {@link CacheManager} bean
     */
    @Bean
    public CacheManager cacheManager() {
        List<CaffeineCache> caches = new ArrayList<>();
        caches.add(new CaffeineCache("pinnedPosts", newBuilder().build()));
        caches.add(buildCache("postFiles", 100, Duration.ofMinutes(1)));
        caches.add(buildCache("avatars", 1_000, Duration.ofMinutes(10)));
        caches.add(buildCache("feeds", 100, Duration.ofMinutes(10)));
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private CaffeineCache buildCache(String name, long maxSize, Duration age) {
        Caffeine<Object, Object> caffeine = newBuilder();
        caffeine.maximumSize(maxSize).expireAfterAccess(age);
        return new CaffeineCache(name, caffeine.build());
    }
}
