package com.academia.academia_api.infra.config;

import com.academia.academia_api.entity.Usuarios;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

@Configuration
public class AuditorAwareImpl {

    @Bean
    public AuditorAware<String> auditorProvider() {

        return () -> {

            Authentication authentication =
                    SecurityContextHolder
                            .getContext()
                            .getAuthentication();

            if (authentication == null
                    || !authentication.isAuthenticated()
                    || authentication.getPrincipal().equals("anonymousUser")) {

                return Optional.of("SYSTEM");
            }

            Usuarios usuario =
                    (Usuarios) authentication.getPrincipal();

            return Optional.of(usuario.getLogin());
        };
    }

}