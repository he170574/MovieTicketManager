package com.movie_theater.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {

        httpSecurity.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> {
                    authorizationManagerRequestMatcherRegistry
                            .requestMatchers("/admin/get-booking-ticket").hasAnyRole("EMPLOYEE","ADMIN")
                            .requestMatchers("/admin/update-status").hasAnyRole("EMPLOYEE","ADMIN")
                            .requestMatchers("/admin/ticket-manager").hasAnyRole("EMPLOYEE","ADMIN")
                            .requestMatchers("/admin/**").hasRole("ADMIN")
                            .requestMatchers("/employee/**").hasRole("EMPLOYEE")
                            .anyRequest().permitAll();
                })
                .formLogin(httpSecurityFormLoginConfigurer -> {
                    httpSecurityFormLoginConfigurer
                            .loginPage("/login")
                            .usernameParameter("username")
                            .passwordParameter("password")
                            .defaultSuccessUrl("/home")
                            .failureHandler((request, response, exception) -> {
                                response.sendError(401, "Invalid Username Or Password");
                            });
                }).logout(httpSecurityLogoutConfigurer -> {
                    httpSecurityLogoutConfigurer
                            .logoutUrl("/logout")
                            .logoutSuccessUrl("/home")
                            .invalidateHttpSession(true)
                            .deleteCookies("JSESSIONID");

                });

        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
