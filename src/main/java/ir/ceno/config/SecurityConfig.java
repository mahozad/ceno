package ir.ceno.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders
        .AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration
        .EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration
        .WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration to set up Spring Security.
 * <p>
 * {@link EnableGlobalMethodSecurity} is to replace authentication null checks in methods with
 * {@link PreAuthorize} that throws {@link AccessDeniedException} if its expression is not evaluated
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true) // for enabling @Preauthorize
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // FIXME: remove
    private static final String USERS_QUERY = "select username, password, true from user where " +
            "username=?";

    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder encoder;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, BCryptPasswordEncoder encoder) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .anyRequest().permitAll() // permit requests to any url
                .and()
                .formLogin()
                .usernameParameter("username")
                .passwordParameter("password")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) ->
                        response.getOutputStream().print(true) // TODO: modify this
                )
                .failureHandler((request, response, exception) ->
                        response.getOutputStream().print("") // TODO: modify this
                )
                .and()
                .rememberMe()
                .rememberMeParameter("rememberMe")
                .and()
                .logout()
                .logoutSuccessHandler((request, response, authentication) ->
                        response.sendRedirect(request.getHeader("referer"))
                )
                .and()
                .cors().disable();
    }

    @Override
    public void configure(WebSecurity security) {
        security.ignoring().antMatchers("/resources/**", "/static/**");
    }
}
