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
                .requestMatchers(HttpMethod.GET, "/catalogo/**").permitAll()
                
                // POST, PUT y DELETE deberían requerir que el token sea válido y el usuario autenticado
                .requestMatchers(HttpMethod.POST, "/catalogo/**").authenticated()
                .requestMatchers(HttpMethod.PUT, "/catalogo/**").authenticated()
                .requestMatchers(HttpMethod.DELETE, "/catalogo/**").authenticated()
                .anyRequest().authenticated()
            )
            .sessionManagement(s->s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
