package com.costuras.catalogo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .sessionManagement(session ->
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .exceptionHandling(ex -> ex
            .accessDeniedHandler(new CustomAccessDeniedHandler())
            )
            .authorizeHttpRequests(auth -> auth

                   
                    .requestMatchers(HttpMethod.GET, "/catalogo/**").permitAll()

                 
                    .requestMatchers(HttpMethod.POST, "/catalogo/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.PUT, "/catalogo/**").hasRole("ADMIN")
                    .requestMatchers(HttpMethod.DELETE, "/catalogo/**").hasRole("ADMIN")

                    .anyRequest().authenticated()
            )

            .addFilterBefore(
                    new JwtAuthenticationFilter(jwtUtil),
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }
}