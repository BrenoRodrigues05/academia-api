package com.academia.academia_api.infra.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecutiryConfigurations {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Login
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()

                        // Alunos
                        .requestMatchers(HttpMethod.GET, "/api/alunos/**")
                        .hasAnyRole("ADMIN", "PERSONAL")

                        .requestMatchers(HttpMethod.POST, "/api/alunos")
                        .hasAnyRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/alunos/**")
                        .hasAnyRole("ADMIN", "ALUNO")

                        .requestMatchers(HttpMethod.DELETE, "/api/alunos/**")
                        .hasRole("ADMIN")

                        // Personais
                        .requestMatchers("/api/personais/**")
                        .hasRole("ADMIN")

                        // Treinos
                        .requestMatchers(HttpMethod.GET, "/api/treinos/**")
                        .hasAnyRole("ADMIN", "PERSONAL", "ALUNO")

                        .requestMatchers(HttpMethod.POST, "/api/treinos")
                        .hasAnyRole("ADMIN", "PERSONAL")

                        .requestMatchers(HttpMethod.PUT, "/api/treinos/**")
                        .hasAnyRole("ADMIN", "PERSONAL")

                        .requestMatchers(HttpMethod.DELETE, "/api/treinos/**")
                        .hasAnyRole("ADMIN", "PERSONAL")

                        .anyRequest().authenticated()
                )
                .build();
    }
}