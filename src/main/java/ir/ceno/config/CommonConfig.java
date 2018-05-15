package ir.ceno.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.tika.Tika;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CommonConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    @Bean
    public ApplicationRunner initializer(EntityManager entityManager) {
        return new ApplicationRunner() {
            @Override
            @Transactional
            public void run(ApplicationArguments args) throws Exception {
                FullTextEntityManager fullTxtEntityManager =
                        Search.getFullTextEntityManager(entityManager);
                fullTxtEntityManager.createIndexer().startAndWait();
            }
        };
    }

    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(Locale.US);
        return localeResolver;
    }

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

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public Tika fileTypeDetector() {
        return new Tika();
    }
}
