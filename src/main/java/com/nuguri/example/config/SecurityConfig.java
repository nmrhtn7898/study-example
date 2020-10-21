package com.nuguri.example.config;

import com.nuguri.example.controller.view.AccountController;
import com.nuguri.example.entity.Account;
import com.nuguri.example.model.AccountAdapter;
import com.nuguri.example.model.Role;
import com.nuguri.example.util.TokenUtil;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserDetailsService userDetailsService;

    private final TokenUtil tokenUtil;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .mvcMatchers("/account/sign-up", "/account/sign-in").anonymous()
                .mvcMatchers(HttpMethod.POST, "/api/v1/account").permitAll()
                .anyRequest().authenticated();
        http
                .exceptionHandling()
                .accessDeniedPage("/");
        http
                .csrf()
                .ignoringAntMatchers("/api/**");
        http
                .headers()
                .frameOptions()
                .sameOrigin();
        http
                .formLogin()
                .usernameParameter("email")
                .loginPage("/account/sign-in")
                .loginProcessingUrl("/account/sign-in")
                .successHandler((request, response, authentication) -> {
                    AccountAdapter accountAdapter = (AccountAdapter) authentication.getPrincipal();
                    String token = tokenUtil.generateJwtToken(accountAdapter.getAccount());
                    Cookie cookie = new Cookie(HttpHeaders.AUTHORIZATION, token);
                    cookie.setHttpOnly(true);
                    response.addCookie(cookie);
                    response.sendRedirect("/");
                })
                .permitAll();
        http
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/account/sign-out", "GET"))
                .logoutSuccessUrl("/account/sign-in");
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
        http
                .addFilterBefore(
                        new OncePerRequestFilter() {
                            @Override
                            protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res,
                                                            FilterChain fc) throws IOException, ServletException {
                                String token = req.getHeader(HttpHeaders.AUTHORIZATION);
                                if (StringUtils.isEmpty(token) || token.startsWith("Bearer ")) {
                                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                                    return;
                                }
                                token = token.substring(7);
                                Claims claims;
                                try {
                                    claims = tokenUtil.getClaimsFromToken(token);
                                } catch (Exception e) {
                                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is Invalid");
                                    return;
                                }
                                if (tokenUtil.isJwtTokenExpired(claims)) {
                                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token is Expired");
                                    return;
                                }
                                AccountAdapter accountAdapter = new AccountAdapter(
                                        Account
                                                .builder()
                                                .id(claims.get("id", Long.class))
                                                .email(claims.get("email", String.class))
                                                .role(claims.get("role", Role.class))
                                                .build()
                                );
                                UsernamePasswordAuthenticationToken authenticationToken =
                                        new UsernamePasswordAuthenticationToken(
                                                accountAdapter,
                                                null,
                                                Collections.singletonList(new SimpleGrantedAuthority(accountAdapter.getAccount().getRole().getFullName()))
                                        );
                                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                                fc.doFilter(req, res);
                            }

                            @Override
                            protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
                                String requestURI = request.getRequestURI();
                                return requestURI.equals("/account/sign-in") || requestURI.equals("/account/sign-up");
                            }
                        }, UsernamePasswordAuthenticationFilter.class
                );
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                .ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations())
                .mvcMatchers("/fonts/**", "/vendor/**", "/scss/**");
    }

}
