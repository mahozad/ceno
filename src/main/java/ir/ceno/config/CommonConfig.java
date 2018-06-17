package ir.ceno.config;

import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration for creating common beans.
 */
@Configuration
public class CommonConfig {

    /**
     * Creates a password encoder bean with the given strength.
     *
     * @param strength the strength of the encoder
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
