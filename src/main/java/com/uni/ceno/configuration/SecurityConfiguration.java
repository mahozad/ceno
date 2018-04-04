package com.uni.ceno.configuration;

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

@EnableWebSecurity
@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Value("${queries.users-query}")
    private String usersQuery;

    private UserDetailsService userDetailsService;
    private BCryptPasswordEncoder encoder;

    public SecurityConfiguration(UserDetailsService userDetailsService, BCryptPasswordEncoder
            encoder) {
        this.userDetailsService = userDetailsService;
        this.encoder = encoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(encoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests().anyRequest().permitAll().
                and().formLogin().usernameParameter("username").passwordParameter("password")
                .loginProcessingUrl("/login").successHandler((request, response, authentication)
                -> response.sendRedirect(request.getHeader("referer"))).
                and().logout().logoutSuccessHandler((request, response, authentication) ->
                response.sendRedirect(request.getHeader("referer"))).
                and().cors().disable().csrf().disable();
    }

    @Override
    public void configure(WebSecurity security) {
        security.ignoring().antMatchers("/resources/**", "/static/**", "/css/**", "/js/**",
                "/images/**", "/fonts/**");
    }
}
