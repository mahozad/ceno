package ir.ceno.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Adds default value and documentation for custom properties used in application.properties and
 * application.yml files.
 * <p>
 * Rebuild is required after adding/editing contents of the class.
 */
@ConfigurationProperties
@Getter
@Setter
@SuppressWarnings({"unused", "WeakerAccess"})
public class PropertiesConfig {

    private int httpPort = 80;
    private String postsFilesPath;
    private String usersFilesPath;
}
