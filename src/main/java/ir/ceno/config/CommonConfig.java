package ir.ceno.config;

import ir.ceno.service.FeedService;
import org.apache.tika.Tika;
import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

@Configuration
public class CommonConfig {

    private FeedService feedService;

    @Autowired
    public CommonConfig(FeedService feedService) {
        this.feedService = feedService;
    }

    @Bean
    public ApplicationRunner initializer(EntityManager entityManager) {
        return new ApplicationRunner() {
            @Override
            @Transactional
            public void run(ApplicationArguments args) throws Exception {
                feedService.initialize();
                FullTextEntityManager fullTxtEntityManager =
                        Search.getFullTextEntityManager(entityManager);
                fullTxtEntityManager.createIndexer().startAndWait();
            }
        };
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Bean
    public Tika fileTypeDetector() {
        return new Tika();
    }

    //@Bean // add http2 support
    //public ConfigurableServletWebServerFactory tomcatCustomizer() {
    //    TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
    //    factory.addConnectorCustomizers(connector -> {
    //        connector.addUpgradeProtocol(new Http2Protocol());
    //    });
    //    return factory;
    //}
}
