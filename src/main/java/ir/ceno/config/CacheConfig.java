package ir.ceno.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import ir.ceno.model.Post;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
        CaffeineCache cache1 = new CaffeineCache("pinnedPosts", Caffeine.newBuilder().build());
        CaffeineCache cache2 = new CaffeineCache("postFiles", Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterAccess(1, TimeUnit.MINUTES)
                .build()
        );
        CaffeineCache cache3 = new CaffeineCache("avatars", Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterAccess(10, TimeUnit.MINUTES)
                .build()
        );
        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(Arrays.asList(cache1, cache2, cache3));
        return cacheManager;
    }
}
