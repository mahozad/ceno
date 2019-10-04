package ir.ceno.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import static java.util.Locale.US;

/**
 * Configuration for adding localization support to the application.
 */
@Configuration
public class LocaleConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }

    /**
     * Creates locale interceptor bean.
     * <p>
     * The url query parameter to change the language is
     * specified with setParamName method of the interceptor.
     *
     * @return {@link LocaleChangeInterceptor} bean
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        interceptor.setParamName("lang");
        return interceptor;
    }

    /**
     * Creates locale resolver bean based on the session.
     * <p>
     * Default locale is specified with setDefaultLocale method of the resolver.
     *
     * @return {@link LocaleResolver} bean
     */
    @Bean
    public LocaleResolver localeResolver() {
        SessionLocaleResolver localeResolver = new SessionLocaleResolver();
        localeResolver.setDefaultLocale(US);
        return localeResolver;
    }
}
