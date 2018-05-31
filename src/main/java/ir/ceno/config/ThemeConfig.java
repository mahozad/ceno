package ir.ceno.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.ResourceBundleThemeSource;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.theme.SessionThemeResolver;
import org.springframework.web.servlet.theme.ThemeChangeInterceptor;

@Configuration
public class ThemeConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(themeChangeInterceptor());
    }

    @Bean
    public ThemeChangeInterceptor themeChangeInterceptor() {
        ThemeChangeInterceptor interceptor = new ThemeChangeInterceptor();
        interceptor.setParamName("theme");
        return interceptor;
    }

    @Bean
    public ThemeResolver themeResolver() {
        SessionThemeResolver themeResolver = new SessionThemeResolver();
        themeResolver.setDefaultThemeName("cyan");
        return themeResolver;
    }

    @Bean
    public ThemeSource themeSource() {
        ResourceBundleThemeSource source = new ResourceBundleThemeSource();
        source.setBasenamePrefix("themes_");
        return source;
    }
}
