package com.springboot.MyTodoList.security;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class WebSecurityConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfiguration.class);

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        logger.info("🔐 Configuring SecurityFilterChain...");

        http
            .csrf().disable()

            // ✅ Session configuration added here
            .sessionManagement(session -> session
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
            )

            .authorizeRequests(authorize -> authorize
                // Public REST API endpoints
                .antMatchers(
                    "/api/signup",
                    "/api/users",
                    "/api/KPIs/**",
                    "/api/todolist/**",
                    "/index.css",
                    "/static/**",
                    "/manifest.json",
                    "/favicon.ico",
                    "/logo192.png",
                    "/logo512.png"
                ).permitAll()

                // React frontend public routes
                .antMatchers("/", "/signup").permitAll()

                // All other requests require authentication
                .anyRequest().authenticated()
            )

            .formLogin(form -> form
                .loginPage("/") // frontend login page
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    response.setStatus(HttpServletResponse.SC_OK);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"message\":\"Login successful\"}");
                })
                .failureHandler((request, response, exception) -> {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\":\"Login failed\"}");
                })
                .permitAll()
            )

            .logout()
            .logoutUrl("/logout")
            .logoutSuccessHandler((req, res, auth) -> {
                logger.info("🚪 LOGOUT triggered for session: {}",
                        req.getSession(false) != null ? req.getSession().getId() : "no session");
                res.setStatus(HttpStatus.OK.value());
                res.setContentType("application/json");
                res.getWriter().write("{\"message\":\"Logout successful\"}");
            });

        logger.info("✅ SecurityFilterChain fully configured.");

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        logger.info("🔑 Initializing BCryptPasswordEncoder...");
        return new BCryptPasswordEncoder();
    }
}
