package com.costuras.catalogo.security;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;



@Configuration @EnableMethodSecurity

public class SecurityConfig {

    private final JwtUtil jwtUtil;

    public SecurityConfig(JwtUtil jwtUtil) { this.jwtUtil = jwtUtil; }
    @Bean
    
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(c->c.disable())
            .authorizeHttpRequests(a->a
                .requestMatchers(HttpMethod.GET,"/catalogo/**").authenticated()
                .requestMatchers(HttpMethod.POST,"/catalogo/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.PUT,"/catalogo/**").hasAuthority("ADMIN")
                .requestMatchers(HttpMethod.DELETE,"/catalogo/**").hasAuthority("ADMIN")
                .anyRequest().authenticated())
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
