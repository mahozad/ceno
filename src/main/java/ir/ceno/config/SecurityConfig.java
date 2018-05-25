package ir.ceno.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders
        .AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration
        .WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Configuration to set up Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${users-query}")
    private String usersQuery;

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
        http.authorizeRequests().anyRequest().permitAll()
                .and().formLogin().usernameParameter("username").passwordParameter("password")
                .loginProcessingUrl("/login")
                .successHandler(
                        (request, response, authentication) ->
                                response.getOutputStream().print(true))
                .failureHandler(
                        (request, response, exception) ->
                                response.getOutputStream().print(""))
                .and().logout()
                .logoutSuccessHandler(
                        (request, response, authentication) ->
                                response.sendRedirect(request.getHeader("referer")))
                .and().cors().disable();
    }

    @Override
    public void configure(WebSecurity security) {
        security.ignoring().antMatchers("/resources/**", "/static/**");
    }
}
