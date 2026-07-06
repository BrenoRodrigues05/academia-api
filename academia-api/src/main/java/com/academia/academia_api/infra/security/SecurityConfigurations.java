package com.academia.academia_api.infra.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfigurations {

    @Autowired
    private SecurityFilter securityFilter;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth

                        // Login
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/auth/register").permitAll()

                        // Alunos
                        .requestMatchers(HttpMethod.GET, "/api/alunos/**")
                        .hasAnyRole("ADMIN", "PERSONAL", "ALUNO")

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
                        .requestMatchers(HttpMethod.GET, "/api/treinos/ativos").hasAnyRole("ADMIN", "PERSONAL")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/inativos").hasAnyRole("ADMIN", "PERSONAL")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/busca-nome").hasAnyRole("ADMIN", "PERSONAL")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/personal/**").hasAnyRole("ADMIN", "PERSONAL")

                        .requestMatchers(HttpMethod.GET, "/api/treinos/aluno/**").hasAnyRole("ADMIN", "PERSONAL", "ALUNO")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/{id}").hasAnyRole("ADMIN", "PERSONAL", "ALUNO")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/me"). hasAnyRole("ADMIN", "PERSONAL", "ALUNO")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/me/historico"). hasAnyRole("ADMIN", "PERSONAL", "ALUNO")


                        .requestMatchers(HttpMethod.GET, "/api/treinos").hasAnyRole("ADMIN", "PERSONAL")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/**").hasAnyRole("ADMIN", "PERSONAL")
                        .requestMatchers(HttpMethod.GET, "/api/treinos/aluno/{id}/historico"). hasAnyRole("SUPERADMIN", "PERSONAL")

                        .requestMatchers(HttpMethod.POST, "/api/treinos")
                        .hasAnyRole("ADMIN", "PERSONAL")

                        .requestMatchers(HttpMethod.PUT, "/api/treinos/**")
                        .hasAnyRole("ADMIN", "PERSONAL")

                        .requestMatchers(HttpMethod.DELETE, "/api/treinos/**")
                        .hasAnyRole("ADMIN", "PERSONAL")

                        // Planos
                        .requestMatchers(HttpMethod.GET, "/api/planos/**").hasAnyRole("ADMIN", "PERSONAL", "ALUNO")
                        .requestMatchers(HttpMethod.POST, "/api/planos").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/planos/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/planos/**").hasRole("ADMIN")

                        // Matrículas
                        .requestMatchers(HttpMethod.GET, "/api/matriculas/**").hasAnyRole("ADMIN", "PERSONAL")
                        .requestMatchers(HttpMethod.POST, "/api/matriculas").hasAnyRole("ADMIN", "PERSONAL", "ALUNO")
                        .requestMatchers(HttpMethod.PATCH, "/api/matriculas/**").hasRole("ADMIN")

                        // Ececuções de treino
                        .requestMatchers(HttpMethod.POST, "/api/execucoes/iniciar/{treinoId}").hasAnyRole("SUPERADMIN","Aluno")

                        // Swagger
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        .requestMatchers("/actuator/health").permitAll()

                        .anyRequest().authenticated()
                )
                .addFilterBefore(
                        securityFilter,
                        UsernamePasswordAuthenticationFilter.class
                )
                .build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration){
         return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}