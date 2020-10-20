package com.nuguri.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/account/sign-up").anonymous()
                .mvcMatchers(HttpMethod.POST, "/api/v1/account", "/api/v1/file").permitAll()
                .anyRequest().authenticated();
        http
                .exceptionHandling()
                .accessDeniedPage("/");
        http
                .csrf()
                .ignoringAntMatchers("/api/**");
        http
                .formLogin()
                .usernameParameter("email")
                .loginPage("/account/sign-in")
                .loginProcessingUrl("/account/sign-in")
                .permitAll();
        http
                .rememberMe()
                .userDetailsService(userDetailsService)
                .rememberMeParameter("remember-me")
                .rememberMeCookieName("remember-me");
        http
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/account/sign-in");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .mvcMatchers("/fonts/**", "/vendor/**", "/scss/**");
    }

}
