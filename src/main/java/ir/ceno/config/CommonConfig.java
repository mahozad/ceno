package ir.ceno.config;

import ir.ceno.model.Post;
import ir.ceno.service.FeedService;
import org.apache.tika.Tika;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Configuration for initializing the application and creating common beans.
 */
@Configuration
public class CommonConfig {

    /**
     * Creates an {@link ApplicationRunner} bean that initializes the application on its startup.
     * <p>
     * feedService is initialized with latest {@link Post post}s of each category and Lucene indexes
     * existing posts for hibernate full text search.
     *
     * @param entityMgr   for hibernate search to create lucene index
     * @param feedService the rss service to initialize feeds
     * @return {@link ApplicationRunner} bean
     */
    @Bean
    public ApplicationRunner initializer(EntityManager entityMgr, FeedService feedService) {
        return new ApplicationRunner() {
            @Override
            @Transactional
            public void run(ApplicationArguments args) throws Exception {
                FullTextEntityManager fullTxtEntityMgr = Search.getFullTextEntityManager(entityMgr);
                fullTxtEntityMgr.createIndexer().startAndWait();
                feedService.initialize();
            }
        };
    }

    /**
     * Creates a password encoder bean with the given strength.
     *
     * @return {@link BCryptPasswordEncoder} bean
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder(@Value("${encoder-strength}") int strength) {
        return new BCryptPasswordEncoder(strength);
    }

    /**
     * Creates a file type detector that is used to probe contents of the user-uploaded files.
     *
     * @return {@link Tika} file type detector bean
     */
    @Bean
    public Tika fileTypeDetector() {
        return new Tika();
    }
}
