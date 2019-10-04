package ir.ceno.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory.DEFAULT_PROTOCOL;

/**
 * Configuration for creating common beans.
 */
@Configuration
public class BeansConfig {

    private static final int ENCODER_STRENGTH = 10;

    @Value("${http-port}")
    private int httpPort;

    @Value("${server.port}")
    private int httpsPort; // default server port

    /**
     * Creates a password encoder bean with the given strength.
     *
     * @return {@link BCryptPasswordEncoder} bean
     */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(ENCODER_STRENGTH);
    }

    /**
     * Creates a bean that configures Tomcat to redirect http requests to https.
     *
     * @return {@link TomcatServletWebServerFactory} bean
     */
    @Bean
    public TomcatServletWebServerFactory httpsRedirectConfig() {
        TomcatServletWebServerFactory tomcatFactory = new TomcatServletWebServerFactory() {
            @Override
            protected void postProcessContext(Context context) {
                SecurityConstraint securityConstraint = new SecurityConstraint();
                securityConstraint.setUserConstraint("CONFIDENTIAL");
                SecurityCollection collection = new SecurityCollection();
                collection.addPattern("/*");
                securityConstraint.addCollection(collection);
                context.addConstraint(securityConstraint);
            }
        };
        Connector connector = new Connector(DEFAULT_PROTOCOL);
        connector.setScheme("http");
        connector.setPort(httpPort);
        connector.setRedirectPort(httpsPort);
        tomcatFactory.addAdditionalTomcatConnectors(connector);
        return tomcatFactory;
    }
}
